package com.example.stylematchia.logic;

import com.example.stylematchia.model.Producto;

import java.util.ArrayList;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProductMatcher {

    public static boolean matchesCategory(Producto producto, String category) {
        if (category == null || category.equalsIgnoreCase("Todo")) {
            return true;
        }

        String text = searchableText(producto);
        switch (normalize(category)) {
            case "camisetas":
                return containsAny(text, "camiseta", "camisetas", "top", "basica");
            case "pantalones":
                return containsAny(text, "pantalon", "pantalones", "jeans", "vaquero", "cargo", "chinos");
            case "zapatillas":
                return containsAny(text, "zapatilla", "zapatillas", "deportivas", "calzado", "sneakers");
            case "sudaderas":
                return containsAny(text, "sudadera", "sudaderas", "hoodie", "capucha");
            case "chaquetas":
                return containsAny(text, "chaqueta", "chaquetas", "cazadora", "abrigo","chaqueton","rebeca");
            default:
                return text.contains(normalize(category));
        }
    }

    public static boolean matchesSearch(Producto producto, String query) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return true;
        }
        return searchableText(producto).contains(normalizedQuery);
    }

    public static List<String> keywords(String text) {
        return Arrays.asList(normalize(text).split("\\W+"));
    }

    public static List<String> detectRequestedCategories(String query) {
        String normalized = normalize(query);
        List<String> categories = new ArrayList<>();
        if (containsAny(normalized, "camiseta", "camisetas", "top")) {
            categories.add("camisetas");
        }
        if (containsAny(normalized, "pantalon", "pantalones", "jeans", "vaquero", "cargo")) {
            categories.add("pantalones");
        }
        if (containsAny(normalized, "zapatilla", "zapatillas", "deportivas", "calzado", "sneakers")) {
            categories.add("zapatillas");
        }
        if (containsAny(normalized, "sudadera", "sudaderas", "hoodie", "capucha")) {
            categories.add("sudaderas");
        }
        if (containsAny(normalized, "chaqueta", "chaquetas", "cazadora", "abrigo")) {
            categories.add("chaquetas");
        }
        return categories;
    }

    public static List<String> detectRequestedColors(String query) {
        String normalized = normalize(query);
        List<String> colors = new ArrayList<>();
        if (containsAny(normalized, "negro", "negra")) {
            colors.add("negro");
        }
        if (containsAny(normalized, "blanco", "blanca")) {
            colors.add("blanco");
        }
        if (containsAny(normalized, "azul")) {
            colors.add("azul");
        }
        if (containsAny(normalized, "verde")) {
            colors.add("verde");
        }
        if (containsAny(normalized, "rojo", "roja")) {
            colors.add("rojo");
        }
        if (containsAny(normalized, "gris")) {
            colors.add("gris");
        }
        if (containsAny(normalized, "beige")) {
            colors.add("beige");
        }
        return colors;
    }

    public static boolean matchesRequestedAttributes(Producto producto, String query) {
        List<String> categories = detectRequestedCategories(query);
        List<String> colors = detectRequestedColors(query);
        String text = searchableText(producto);

        boolean categoryOk = categories.isEmpty();
        for (String category : categories) {
            if (matchesCategory(producto, category)) {
                categoryOk = true;
                break;
            }
        }

        boolean colorOk = colors.isEmpty();
        for (String color : colors) {
            if (text.contains(color)) {
                colorOk = true;
                break;
            }
        }

        return categoryOk && colorOk;
    }

    public static String searchableText(Producto producto) {
        return normalize(producto.getId() + " "
                + producto.getNombre() + " "
                + producto.getMarca() + " "
                + producto.getDescripcion() + " "
                + producto.getPrecio());
    }

    public static String normalize(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase(Locale.ROOT).trim();
    }

    private static boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(normalize(word))) {
                return true;
            }
        }
        return false;
    }
}
