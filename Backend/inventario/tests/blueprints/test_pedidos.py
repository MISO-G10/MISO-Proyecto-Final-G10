import pytest
import uuid
from datetime import datetime, timedelta
from src.db.session import SessionLocal
from src.models.producto import Producto, Categoria
from src.models.pedido import Pedido, PedidoProducto, EstadoPedido
from unittest.mock import patch

