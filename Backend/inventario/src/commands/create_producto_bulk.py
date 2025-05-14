from google.cloud import pubsub_v1
from flask import jsonify
from marshmallow import ValidationError, Schema, fields
import json

from .create_producto import CreateProductoSchema
from .base_command import BaseCommand

class CreateProductoBulkCommand(BaseCommand):
    def __init__(self, usuario, productos):
        self.usuario = usuario
        self.productos = productos
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('your-project-id', 'producto-registro')

    def execute(self):
        try:
            # Validar cada producto
            validated_products = []
            for producto in self.productos:
                schema = CreateProductoSchema().load(producto)
                validated_products.append(schema)

            # Publicar cada producto validado al topic
            for producto in validated_products:
                # Añadir información del usuario que realiza el registro
                mensaje = {
                    "usuario_id": self.usuario.id,
                    "producto": producto
                }
                
                # Convertir el mensaje a string
                message_data = json.dumps(mensaje).encode('utf-8')
                
                # Publicar al topic
                future = self.publisher.publish(
                    self.topic_path,
                    message_data
                )
                future.result()  # Esperar confirmación

            return {
                "message": f"Se han enviado {len(validated_products)} productos para procesamiento",
                "total": len(validated_products)
            }

        except ValidationError as e:
            return {"error": "Error en la validación de productos", "details": e.messages}, 400
        except Exception as e:
            return {"error": "Error al procesar los productos", "details": str(e)}, 500
