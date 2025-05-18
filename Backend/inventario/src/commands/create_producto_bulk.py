from flask import jsonify
from marshmallow import ValidationError, Schema, fields
import json
from .create_producto import Create, CreateProductoSchema

from .create_producto import CreateProductoSchema
from .base_command import BaseCommand


class CreateProductoBulkCommand(BaseCommand):
    def __init__(self, usuario, productos):
        self.usuario = usuario
        self.productos = productos

    def execute(self):
        try:
            # Validar cada producto
            validated_products = []
            errors = []

            for i, producto in enumerate(self.productos):
                try:
                    CreateProductoSchema().load(producto)

                    validated_products.append(producto)
                except ValidationError as e:
                    errors.append({"index": i, "error": e.messages})

            print(validated_products)

            if errors:
                return {"error": "Error de validación", "details": errors}, 400

            # Procesar cada producto validado
            created_products = []
            for producto in validated_products:
                try:
                    command = Create(self.usuario, producto)
                    result = command.execute()
                    if isinstance(result, tuple):
                        return result  # Propagar error si ocurre
                    created_products.append(result)
                except Exception as e:
                    return {"error": str(e)}, 400  # Error de negocio

            return {
                "message": f"Se han creado {len(created_products)} productos exitosamente",
                "total": len(created_products),
                "products": created_products
            }, 201  # Creado exitosamente

        except ValidationError as e:
            return {"error": "Error en la validación de productos", "details": e.messages}, 400
        except Exception as e:
            return {"error": "Error al procesar los productos", "details": str(e)}, 500
