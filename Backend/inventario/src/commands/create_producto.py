from flask import jsonify
from marshmallow import ValidationError, Schema, fields, validates_schema
from datetime import datetime
import hashlib

from src.db.session import SessionLocal
from src.errors.errors import InvalidProductoData
from src.models.producto import Producto, Categoria
from .assign_producto_bodega import AssignProductoBodega
from .base_command import BaseCommand
import uuid


class CreateProductoSchema(Schema):
    nombre = fields.Str(required=True)
    descripcion = fields.Str(required=True)
    perecedero = fields.Bool(required=True)
    fechaVencimiento = fields.DateTime(required=False, allow_none=True)
    valorUnidad = fields.Float(required=True)
    tiempoEntrega = fields.DateTime(required=True)
    condicionAlmacenamiento = fields.Str(required=True)
    reglasLegales = fields.Str(required=True)
    reglasComerciales = fields.Str(required=True)
    reglasTributarias = fields.Str(required=True)
    categoria = fields.Str(required=True)
    bodega = fields.Str(required=False, allow_none=True)
    cantidad = fields.Int(required=False, allow_none=True)
    fabricante_id = fields.Str(required=True)

    @validates_schema
    def validate_categoria(self, data, **kwargs):
        try:
            categoria_str = data["categoria"]
            # Intentar encontrar la categoría por nombre o por valor
            try:
                Categoria[categoria_str]  # Buscar por nombre (ALIMENTOS_BEBIDAS)
            except KeyError:
                # Si no se encuentra por nombre, buscar por valor
                if not any(c.value == categoria_str for c in Categoria):
                    raise KeyError
                # Si se encuentra por valor, convertir al nombre
                for c in Categoria:
                    if c.value == categoria_str:
                        data["categoria"] = c.name
                        break
        except KeyError:
            raise ValidationError(f"Categoría inválida. Opciones válidas: {', '.join([c.name for c in Categoria])}")

    @validates_schema
    def validate_fecha_vencimiento(self, data, **kwargs):
        # Se valida que si se tiene sleccionado que el producto es perecedero se solicite la fecha
        if data.get("perecedero") and "fechaVencimiento" not in data:
            raise ValidationError(
                {"fechaVencimiento": ["La fecha de vencimiento es requerida para productos perecederos"]})

    @validates_schema
    def validate_cantidad(self, data, **kwargs):
        if "bodega" in data:
            if not data.get("bodega"):
                raise ValidationError(
                    {"bodega": ["La bodega es requerida para asignar un producto"]})
            if 'cantidad' not in data and not data.get("cantidad"):
                raise ValidationError(
                    {"cantidad": ["La cantidad es requerida para asignar un producto"]})

            else:
                if data.get("cantidad") <= 0:
                    raise ValidationError(
                        {"cantidad": ["La cantidad debe ser mayor a 0"]})


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
            # Se consulta la BDD para ver si ya existe un producto con el mismo nombre asociado al mismo fabricante
            producto_existente = db.query(Producto).filter_by(sku=sku).first()
            if producto_existente:
                return {"error": "El producto ya existe para este fabricante"}, 400

            # Se instancia un nuevo producto
            fecha_vencimiento = schema.get('fechaVencimiento') if schema.get('perecedero') else None

            nuevo_producto = Producto(
                sku=sku,
                nombre=schema['nombre'],
                descripcion=schema['descripcion'],
                perecedero=schema['perecedero'],
                fechaVencimiento=fecha_vencimiento,
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

            if 'bodega' in schema and 'cantidad' in schema:
                bodega = AssignProductoBodega(schema['bodega'], {
                    "producto_id": str(nuevo_producto.id),
                    "cantidad": schema['cantidad'],
                }).execute()

                if isinstance(bodega, tuple) and len(bodega) == 2:
                    return bodega[0], bodega[1]

            return {
                "id": nuevo_producto.id,
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
