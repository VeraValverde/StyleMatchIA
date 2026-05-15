package com.example.stylematchia.data.remote;

import com.example.stylematchia.BuildConfig;
import com.example.stylematchia.model.Producto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenRouterRecommendationService {

    public static class RecommendationResult {
        public final String summary;
        public final List<String> productIds;

        public RecommendationResult(String summary, List<String> productIds) {
            this.summary = summary;
            this.productIds = productIds;
        }
    }

    public interface CallbackResult {
        void onSuccess(RecommendationResult result);

        void onError(String message);
    }

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public boolean isConfigured() {
        return BuildConfig.OPENROUTER_API_KEY != null && !BuildConfig.OPENROUTER_API_KEY.trim().isEmpty();
    }

    public void recommend(String prompt, List<Producto> productos, CallbackResult callback) {
        if (!isConfigured()) {
            callback.onError("OpenRouter no esta configurado.");
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("model", BuildConfig.OPENROUTER_MODEL);
            body.put("temperature", 0.2);
            body.put("max_tokens", 300);
            body.put("messages", buildMessages(prompt, productos));

            Request request = new Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + BuildConfig.OPENROUTER_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://stylematchia.local")
                    .addHeader("X-OpenRouter-Title", "StyleMatchIA")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, java.io.IOException e) {
                    callback.onError("No se pudo conectar con OpenRouter.");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        if (!response.isSuccessful()) {
                            callback.onError("OpenRouter devolvio error " + response.code() + ".");
                            return;
                        }

                        JSONObject json = new JSONObject(responseBody);
                        JSONArray choices = json.getJSONArray("choices");
                        if (choices.length() == 0) {
                            callback.onError("OpenRouter no devolvio recomendaciones.");
                            return;
                        }

                        String content = choices.getJSONObject(0)
                                .getJSONObject("message")
                                .optString("content", "");
                        RecommendationResult result = parseContent(content);
                        if (result.productIds.isEmpty()) {
                            callback.onError("OpenRouter no encontro coincidencias claras.");
                            return;
                        }
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onError("No se pudo leer la respuesta de OpenRouter.");
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("No se pudo preparar la peticion de OpenRouter.");
        }
    }

    private JSONArray buildMessages(String prompt, List<Producto> productos) throws Exception {
        JSONArray messages = new JSONArray();

        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content",
                "Eres un asistente de recomendacion de moda. " +
                "Debes priorizar la descripcion de los productos por encima del resto. " +
                        "Si el usuario pide un color o un tipo de prenda concreto, no devuelvas productos que no cumplan eso. " +
                        "Responde solo en JSON valido con esta forma exacta: " +
                        "{\"summary\":\"texto corto en espanol\",\"product_ids\":[\"id1\",\"id2\"]}. " +
                        "No inventes ids. Usa solo ids de la lista recibida.");
        messages.put(system);

        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", buildPrompt(prompt, productos));
        messages.put(user);

        return messages;
    }

    private String buildPrompt(String prompt, List<Producto> productos) {
        StringBuilder builder = new StringBuilder();
        builder.append("Peticion del usuario: ").append(prompt).append("\n\n");
        builder.append("Catalogo disponible:\n");

        int limit = Math.min(productos.size(), 30);
        for (int i = 0; i < limit; i++) {
            Producto producto = productos.get(i);
            builder.append("- id: ").append(producto.getId())
                    .append(" | nombre: ").append(producto.getNombre())
                    .append(" | marca: ").append(producto.getMarca())
                    .append(" | precio: ").append(producto.getPrecio())
                    .append(" | descripcion: ").append(producto.getDescripcion())
                    .append("\n");
        }

        builder.append("\nDevuelve solo los ids de los productos que mejor encajan, priorizando la descripcion.");
        return builder.toString();
    }

    private RecommendationResult parseContent(String content) throws Exception {
        String cleaned = content == null ? "" : content.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```json", "")
                    .replaceFirst("^```", "")
                    .replaceFirst("```$", "")
                    .trim();
        }

        JSONObject json = new JSONObject(cleaned);
        String summary = json.optString("summary", "Estas son las prendas que mejor encajan con lo que has pedido.");
        JSONArray idsArray = json.optJSONArray("product_ids");
        List<String> ids = new ArrayList<>();
        if (idsArray != null) {
            for (int i = 0; i < idsArray.length(); i++) {
                String id = idsArray.optString(i, "").trim();
                if (!id.isEmpty()) {
                    ids.add(id);
                }
            }
        }
        return new RecommendationResult(summary, ids);
    }
}
