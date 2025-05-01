from src.db.session import SessionLocal
from src.models.producto import Producto
from .base_command import BaseCommand

class ListProductos(BaseCommand):
    def __init__(self):
        pass

    def execute(self):
        db = SessionLocal()
        try:
            productos = db.query(Producto).all()

            productos_list = []
            for producto in productos:
                productos_list.append({
                    "sku": producto.sku,
                    "nombre": producto.nombre,
                    "descripcion": producto.descripcion,
                    "perecedero": producto.perecedero,
                    "fechaVencimiento": producto.fechaVencimiento.isoformat() if producto.fechaVencimiento else None,
                    "valorUnidad": producto.valorUnidad,
                    "tiempoEntrega": producto.tiempoEntrega.isoformat() if producto.tiempoEntrega else None,
                    "condicionAlmacenamiento": producto.condicionAlmacenamiento,
                    "reglasLegales": producto.reglasLegales,
                    "reglasComerciales": producto.reglasComerciales,
                    "reglasTributarias": producto.reglasTributarias,
                    "categoria": producto.categoria.name if producto.categoria else None,
                    "fabricante_id": producto.fabricante_id,
                    "createdAt": producto.createdAt.isoformat() if producto.createdAt else None
                })

            return productos_list

        except Exception as e:
            db.rollback()
            raise e
