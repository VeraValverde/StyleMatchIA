package com.example.stylematchia.data;

import com.example.stylematchia.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class CatalogoDemo {

    public static List<Producto> crearCatalogoInicial() {
        List<Producto> productos = new ArrayList<>();
        productos.add(crear("hoodie_tech_fleece", "Hoodie Tech Fleece", "Nike", 89.99,
                "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/492a7364-6ea6-4a2d-b9f7-cd6d7747b5b0/M+NK+TF+HD+FZ+WR.png",
                "https://www.nike.com/es/",
                "Sudadera hoodie negra con capucha Nike Tech Fleece para un look comodo, moderno, casual y de invierno."));
        productos.add(crear("camisa_oxford", "Camisa Oxford", "Zara", 29.95,
                "https://static.zara.net/assets/public/2f7d/6827/7cbc4c0bb0d6/2f2f2299d3bc/05584303403-e1/05584303403-e1.jpg?ts=1745230467248&w=824",
                "https://www.zara.com/es/",
                "Camisa azul clara ligera para outfit elegante, formal, oficina o smart casual de primavera."));
        productos.add(crear("jeans_straight", "Jeans Straight", "Levi's", 59.99,
                "https://lsco.scene7.com/is/image/lsco/A19590016-front-pdp?fmt=jpeg&qlt=70",
                "https://www.levi.com/ES/es_ES/",
                "Pantalon vaquero jeans azul recto y comodo para el dia a dia y looks casuales."));
        productos.add(crear("air_max", "Zapatillas Air Max", "Nike", 129.99,
                "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/efc4924f-4079-40cc-80e8-5aa635e34618/AIR+MAX+90.png",
                "https://www.nike.com/es/",
                "Zapatillas deportivas negras, comodas y urbanas para ropa sport y streetwear."));
        productos.add(crear("cargo_bershka", "Pantalon Cargo", "Bershka", 29.99,
                "https://static.bershka.net/assets/public/9dd0/e10d/352744c4b82a/4dc7f2361782/05341242505-a2o/05341242505-a2o.jpg?ts=1739268398347&w=850",
                "https://www.bershka.com/es/",
                "Pantalon cargo verde relajado con bolsillos laterales y estilo urbano streetwear de otono."));
        productos.add(crear("camiseta_basic", "Camiseta Basic", "Pull&Bear", 12.99,
                "https://static.pullandbear.net/assets/public/6c13/9eb1/9fd64e4ca0c1/11fb0656d8a1/03241331250-A6M/03241331250-A6M.jpg?ts=1747045149717&w=750&f=auto",
                "https://www.pullandbear.com/es/",
                "Camiseta basica blanca barata, fresca y facil de combinar para verano."));
        return productos;
    }

    private static Producto crear(String id, String nombre, String marca, double precio,
                                  String imagenUrl, String enlaceTienda, String descripcion) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setPrecio(precio);
        producto.setImagenUrl(imagenUrl);
        producto.setEnlaceTienda(enlaceTienda);
        producto.setDescripcion(descripcion);
        return producto;
    }
}
