package com.salesiana.inventory_system.controller;

import com.salesiana.inventory_system.entity.Producto;
import com.salesiana.inventory_system.service.ProductoService;
import com.salesiana.inventory_system.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listarProductos(@RequestParam(required = false) String stock, Model model) {
        try {
            System.out.println("=== CARGANDO LISTA DE PRODUCTOS ===");
            System.out.println("Filtro de stock: " + (stock != null ? stock : "ninguno"));
            
            List<Producto> productos;
            long productosStockBajo = 0;
            long productosAgotados = 0;

            // Cargar productos según el filtro
            if(stock != null) {
                switch(stock) {
                    case "bajo":
                        productos = productoService.obtenerProductosStockBajo();
                        System.out.println("🔍 Filtro: Productos con stock bajo");
                        break;
                    case "critico":
                        productos = productoService.obtenerProductosStockBajo();
                        System.out.println("🔍 Filtro: Productos con stock crítico");
                        break;
                    case "agotado":
                        productos = productoService.obtenerProductosAgotados();
                        System.out.println("🔍 Filtro: Productos agotados");
                        break;
                    default:
                        productos = productoService.obtenerTodosProductos();
                        System.out.println("🔍 Sin filtro específico, cargando todos los productos");
                }
            } else {
                productos = productoService.obtenerTodosProductos();
                System.out.println("🔍 Cargando todos los productos");
            }
            
            // Obtener estadísticas
            productosStockBajo = productoService.obtenerProductosStockBajo().size();
            productosAgotados = productoService.obtenerProductosAgotados().size();

            model.addAttribute("productos", productos != null ? productos : Collections.emptyList());
            model.addAttribute("productosStockBajo", productosStockBajo);
            model.addAttribute("productosAgotados", productosAgotados);
            model.addAttribute("stockFilter", stock);
            
            System.out.println("✅ Productos cargados exitosamente. Total: " + (productos != null ? productos.size() : 0));
            System.out.println("📊 Estadísticas: Stock bajo=" + productosStockBajo + ", Agotados=" + productosAgotados);

            return "productos/lista";
        } catch(Exception e) {
            System.err.println("❌ Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
            
            // Agregar atributos con valores por defecto seguros
            model.addAttribute("error", "Error al cargar los productos: " + e.getMessage());
            model.addAttribute("productos", Collections.emptyList());
            model.addAttribute("productosStockBajo", 0L);
            model.addAttribute("productosAgotados", 0L);
            model.addAttribute("stockFilter", stock);
            
            // Mostrar mensaje de error en consola detallado
            System.err.println("⚠️ Detalles del error:");
            e.printStackTrace();
            
            return "productos/lista";
        }
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO NUEVO PRODUCTO ===");
            
            model.addAttribute("producto", new Producto());
            model.addAttribute("categorias", categoriaService.obtenerTodasCategorias());
            
            System.out.println("✅ Formulario de creación de producto cargado exitosamente");
            System.out.println("📊 Categorías disponibles: " + categoriaService.obtenerTodasCategorias().size());
            
            return "productos/form";
        } catch(Exception e) {
            System.err.println("❌ Error al cargar formulario de nuevo producto: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/productos";
        }
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== GUARDANDO PRODUCTO ===");
            System.out.println("ID: " + (producto.getId() != null ? producto.getId() : "nuevo"));
            System.out.println("Nombre: " + producto.getNombre());
            System.out.println("Código: " + producto.getCodigo());
            System.out.println("Categoría ID: " + (producto.getCategoria() != null ? producto.getCategoria().getId() : "null"));
            System.out.println("Stock actual: " + producto.getStockActual());
            System.out.println("Stock mínimo: " + producto.getStockMinimo());

            // Validar datos básicos
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre del producto es requerido");
                System.out.println("❌ Error: Nombre del producto vacío");
                return "redirect:/productos/nuevo";
            }

            if (producto.getCodigo() == null || producto.getCodigo().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El código del producto es requerido");
                System.out.println("❌ Error: Código del producto vacío");
                return "redirect:/productos/nuevo";
            }

            Producto productoGuardado = productoService.guardarProducto(producto);
            
            System.out.println("✅ Producto guardado exitosamente. ID: " + productoGuardado.getId());
            System.out.println("✅ Nombre: " + productoGuardado.getNombre());
            System.out.println("✅ Código: " + productoGuardado.getCodigo());
            
            redirectAttributes.addFlashAttribute("success", "Producto guardado correctamente");
            return "redirect:/productos";
        } catch(Exception e) {
            System.err.println("❌ Error al guardar producto: " + e.getMessage());
            e.printStackTrace();
            
            redirectAttributes.addFlashAttribute("error", "Error al guardar producto: " + e.getMessage());
            // Conservar los datos del formulario para no perder la información
            redirectAttributes.addFlashAttribute("producto", producto);
            return "redirect:/productos/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        try {
            System.out.println("=== CARGANDO FORMULARIO DE EDICIÓN ===");
            System.out.println("ID del producto a editar: " + id);
            
            Optional<Producto> producto = productoService.obtenerProductoPorId(id);
            if(producto.isPresent()) {
                model.addAttribute("producto", producto.get());
                model.addAttribute("categorias", categoriaService.obtenerTodasCategorias());
                System.out.println("✅ Producto encontrado para edición: " + producto.get().getNombre());
                return "productos/form";
            } else {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                model.addAttribute("error", "Producto no encontrado con ID: " + id);
                return "redirect:/productos";
            }
        } catch(Exception e) {
            System.err.println("❌ Error al cargar producto para editar: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("error", "Error al cargar producto: " + e.getMessage());
            return "redirect:/productos";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ELIMINANDO PRODUCTO ===");
            System.out.println("ID: " + id);
            
            Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);
            if (!productoOpt.isPresent()) {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado con ID: " + id);
                return "redirect:/productos";
            }
            
            productoService.eliminarProducto(id);
            System.out.println("✅ Producto eliminado exitosamente: " + productoOpt.get().getNombre());
            redirectAttributes.addFlashAttribute("success", "Producto '" + productoOpt.get().getNombre() + "' eliminado correctamente");
            return "redirect:/productos";
        } catch(Exception e) {
            System.err.println("❌ Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
            return "redirect:/productos";
        }
    }

    @GetMapping("/buscar")
    public String buscarProductos(@RequestParam String q, Model model) {
        try {
            System.out.println("=== BÚSQUEDA DE PRODUCTOS ===");
            System.out.println("Término de búsqueda: " + q);
            
            List<Producto> productos = productoService.buscarProductos(q);
            long productosStockBajo = productoService.obtenerProductosStockBajo().size();
            long productosAgotados = productoService.obtenerProductosAgotados().size();

            model.addAttribute("productos", productos != null ? productos : Collections.emptyList());
            model.addAttribute("terminoBusqueda", q);
            model.addAttribute("productosStockBajo", productosStockBajo);
            model.addAttribute("productosAgotados", productosAgotados);
            
            System.out.println("🔍 Resultados de búsqueda: " + (productos != null ? productos.size() : 0) + " productos encontrados");

            return "productos/lista";
        } catch(Exception e) {
            System.err.println("❌ Error en búsqueda de productos: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("error", "Error en la búsqueda: " + e.getMessage());
            model.addAttribute("productos", Collections.emptyList());
            model.addAttribute("terminoBusqueda", q);
            model.addAttribute("productosStockBajo", 0L);
            model.addAttribute("productosAgotados", 0L);
            
            return "productos/lista";
        }
    }
    
    // ✅ NUEVO ENDPOINT: Vista de detalle de producto
    @GetMapping("/detalle/{id}")
    public String verDetalleProducto(@PathVariable Integer id, Model model) {
        try {
            System.out.println("=== DETALLE DE PRODUCTO ===");
            System.out.println("ID: " + id);
            
            Optional<Producto> productoOpt = productoService.obtenerProductoPorId(id);
            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                model.addAttribute("producto", producto);
                System.out.println("✅ Detalle de producto cargado: " + producto.getNombre());
                return "productos/detalle";
            } else {
                System.out.println("❌ Producto no encontrado con ID: " + id);
                model.addAttribute("error", "Producto no encontrado");
                return "redirect:/productos";
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar detalle de producto: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el detalle del producto");
            return "redirect:/productos";
        }
    }
}