from src.db.session import SessionLocal
from src.models.producto import Producto
from src.models.inventario_bodega import InventarioBodega
from .base_command import BaseCommand
from sqlalchemy.orm import joinedload


class ListProductos(BaseCommand):
    def __init__(self):
        pass

    def execute(self):
        db = SessionLocal()
        try:
            # Consultar productos con al menos un registro en InventarioBodega
            productos = (
                db.query(Producto)
                .join(InventarioBodega, Producto.id == InventarioBodega.producto_id)
                .filter(InventarioBodega.cantidad > 0)
                .options(joinedload(Producto.bodega_productos))
                .group_by(Producto.id)
                .all()
            )

            productos_list = []
            for producto in productos:
                #calculo de cantidad total de productos en todas las bodegas
                cantidad_total = sum([ib.cantidad for ib in producto.bodega_productos])
                productos_list.append({
                    "id": producto.id,
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
                    "createdAt": producto.createdAt.isoformat() if producto.createdAt else None,
                    "cantidad_total": cantidad_total
                })

            return productos_list

        except Exception as e:
            db.rollback()
            raise e
