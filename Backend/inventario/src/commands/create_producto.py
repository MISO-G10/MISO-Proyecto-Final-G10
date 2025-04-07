from flask import jsonify
from marshmallow import ValidationError, Schema, fields, validates_schema
from datetime import datetime
import hashlib

from src.db.session import SessionLocal
from src.errors.errors import InvalidProductoData
from src.models.producto import Producto, Categoria
from .base_command import BaseCommand
import uuid


class CreateProductoSchema(Schema):
    nombre = fields.Str(required=True)
    descripcion = fields.Str(required=True)
    perecedero = fields.Bool(required=True)
    fechaVencimiento = fields.DateTime(required=True)
    valorUnidad = fields.Float(required=True)
    tiempoEntrega = fields.DateTime(required=True)
    condicionAlmacenamiento = fields.Str(required=True)
    reglasLegales = fields.Str(required=True)
    reglasComerciales = fields.Str(required=True)
    reglasTributarias = fields.Str(required=True)
    categoria = fields.Str(required=True)
    fabricante_id = fields.Str(required=True)
    
    @validates_schema
    def validate_categoria(self, data, **kwargs):
        try:
            categoria_str = data["categoria"]
            Categoria[categoria_str]
        except KeyError:
            raise ValidationError(f"Categoría inválida. Opciones válidas: {', '.join([c.name for c in Categoria])}")


class Create(BaseCommand):
    def __init__(self, usuario, data):
        self.usuario = usuario
        self.data = data

    def execute(self):
        schema = self.safe_payload()
        db = SessionLocal()

        try:
            # Se genera un SKU único al concatenar el id del fabricante y un hash del nombre del producto
            hash_nombre = hashlib.sha256(self.data["nombre"].encode()).hexdigest()[:6]  # Solo 6 caracteres
            sku = f"{self.data['fabricante_id']}-{hash_nombre}".upper()
            #Se consulta la BDD para ver si ya existe un producto con el mismo nombre asociado al mismo fabricante
            producto_existente = db.query(Producto).filter_by(sku=sku).first()
            if producto_existente:
                return {"error": "El producto ya existe para este fabricante"}, 400
            
            # Se instancia un nuevo producto
            nuevo_producto = Producto(
                sku=sku,
                nombre=schema['nombre'],
                descripcion=schema['descripcion'],
                perecedero=schema['perecedero'],
                fechaVencimiento=schema['fechaVencimiento'],
                valorUnidad=schema['valorUnidad'],
                tiempoEntrega=schema['tiempoEntrega'],
                condicionAlmacenamiento=schema['condicionAlmacenamiento'],
                reglasLegales=schema['reglasLegales'],
                reglasComerciales=schema['reglasComerciales'],
                reglasTributarias=schema['reglasTributarias'],
                categoria=Categoria[schema['categoria']],
                fabricante_id=schema['fabricante_id']
            )

            db.add(nuevo_producto)
            db.commit()

            return {
                "sku": nuevo_producto.sku,
                "createdAt": nuevo_producto.createdAt.isoformat()
            }

        except Exception as e:
            db.rollback()
            raise e

    def safe_payload(self):
        try:
            schema = CreateProductoSchema().load(self.data)
            return schema
        except ValidationError as e:
            raise InvalidProductoData(e.messages)