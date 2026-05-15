package com.example.stylematchia.logic;

import com.example.stylematchia.model.Producto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AiRecommender {

    public static class Result {
        public final boolean valid;
        public final String message;
        public final List<Producto> productos;

        public Result(boolean valid, String message, List<Producto> productos) {
            this.valid = valid;
            this.message = message;
            this.productos = productos;
        }
    }

    public static Result recommend(String prompt, List<Producto> productos) {
        String query = ProductMatcher.normalize(prompt);
        if (query.length() < 3) {
            return new Result(false, "Peticion invalida. Debes pedir algo relacionado con ropa.", new ArrayList<>());
        }

        if (!hasFashionIntent(query)) {
            return new Result(false, "Peticion invalida. Debes pedir algo relacionado con ropa.", new ArrayList<>());
        }

        List<Producto> matches = new ArrayList<>();
        for (Producto producto : productos) {
            if (ProductMatcher.matchesRequestedAttributes(producto, query) && scoreProduct(query, producto) > 0) {
                matches.add(producto);
            }
        }

        if (matches.isEmpty()) {
            return new Result(true, "He entendido tu peticion, pero no tengo prendas que coincidan ahora mismo.", matches);
        }

        return new Result(true, "Estas son las prendas que mejor encajan con lo que has pedido.", matches);
    }

    private static boolean hasFashionIntent(String query) {
        List<String> fashionWords = Arrays.asList(
                "ropa", "look", "outfit", "camiseta", "pantalon", "jeans", "zapatillas",
                "sudadera", "hoodie", "chaqueta", "vestido", "falda", "elegante", "casual",
                "streetwear", "deportiva", "deportivo", "verano", "invierno", "negro",
                "blanco", "azul", "barato", "precio", "menos", "maximo"
        );
        for (String word : fashionWords) {
            if (query.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private static int scoreProduct(String query, Producto producto) {
        List<String> keywords = extractKeywords(query);
        if (keywords.isEmpty()) {
            return 0;
        }

        String searchableText = ProductMatcher.searchableText(producto) + " " + expandDescriptionAliases(producto);
        int score = 0;

        for (String keyword : keywords) {
            if (searchableText.contains(keyword)) {
                score++;
            }
        }

        Double maxPrice = extractMaxPrice(query);
        if (maxPrice != null && producto.getPrecio() <= maxPrice) {
            score++;
        }

        return score;
    }

    private static String expandDescriptionAliases(Producto producto) {
        String text = ProductMatcher.searchableText(producto);
        StringBuilder aliases = new StringBuilder();
        if (text.contains("sport") || text.contains("deport")) {
            aliases.append(" deporte deportiva deportivo");
        }
        if (text.contains("streetwear") || text.contains("urbano")) {
            aliases.append(" urbano urbana calle");
        }
        if (text.contains("elegante") || text.contains("formal")) {
            aliases.append(" formal oficina cita");
        }
        if (text.contains("casual")) {
            aliases.append(" comodo comoda diario");
        }
        if (text.contains("zapatillas")) {
            aliases.append(" deportivas calzado");
        }
        if (text.contains("pantalon") || text.contains("jeans")) {
            aliases.append(" vaquero cargo");
        }
        if (text.contains("sudadera") || text.contains("hoodie")) {
            aliases.append(" capucha");
        }
        return aliases.toString();
    }

    private static List<String> extractKeywords(String query) {
        List<String> stopWords = Arrays.asList(
                "quiero", "busco", "buscar", "necesito", "ropa", "look", "outfit", "algo",
                "para", "con", "de", "del", "la", "el", "los", "las", "un", "una", "y", "o",
                "que", "me", "gustaria", "recomienda", "recomendaciones", "menos", "maximo",
                "precio", "barato", "barata", "euros"
        );
        List<String> keywords = new ArrayList<>();
        for (String word : query.split("\\W+")) {
            String normalized = ProductMatcher.normalize(word);
            if (normalized.length() >= 4 && !stopWords.contains(normalized) && !normalized.matches("\\d+")) {
                keywords.add(normalized);
            }
        }
        return keywords;
    }

    private static Double extractMaxPrice(String query) {
        String[] words = query.replace(",", ".").split("\\s+");
        boolean mentionsPrice = query.contains("menos") || query.contains("maximo")
                || query.contains("barato") || query.contains("precio");
        if (!mentionsPrice) {
            return null;
        }

        for (String word : words) {
            try {
                return Double.parseDouble(word.replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
