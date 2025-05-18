import { Producto, Categoria } from '../../productos/models/producto';

export interface Pedido {
    id: string;
    direccion: string;
    estado: EstadoPedido;
    fechaEntrega: string;
    fechaSalida: string;
    createdAt: string;
    updatedAt: string;
    usuario_id: string;
    vendedor_id: string;
    valor: number;
    pedido_productos: PedidoProducto[];
}

export interface PedidoProducto {
    id: string;
    pedido_id: string;
    producto_id: string;
    cantidad: number;
    valorUnitario: number;
    subtotal: number;
    createdAt: string;
    updatedAt: string;
    producto: Producto;
}

export interface EstadoPedido {
    id?: string;
    nombre?: string;
    descripcion?: string;
}