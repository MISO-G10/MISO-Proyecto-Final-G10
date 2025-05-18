import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DeliveryRoute } from './models/entrega-ruta.model';
import { formatDate } from '@angular/common';
import { environment } from '../../../../environment/environment';
import { RutaService } from '../../../core/services/rutas.services';
import { PedidoService } from '../../../core/services/pedidos.services';
import { Pedido } from '../pedidos/models/pedido';
import { Ruta } from './models/ruta';

@Component({
  selector: 'app-rutas',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './rutas.component.html',
  styleUrls: ['./rutas.component.scss']
})
export class RutasComponent implements OnInit {
  // Señales para gestionar el estado
  routes = signal<DeliveryRoute[]>([]);
  filteredRoutes = signal<DeliveryRoute[]>([]);
  selectedDate = signal(new Date());
  hasRoutes = signal(false);
  private readonly rutaService = inject(RutaService);
  private readonly pedidoService = inject(PedidoService);
  
  // Constante para el número máximo de pedidos por ruta
  private readonly MAX_PEDIDOS_POR_RUTA = 3;

  // Traducciones
  readonly translations = {
    title: $localize`:@@delivery.routes.title:Rutas de Entrega`,
    date_selector: $localize`:@@delivery.routes.date_selector:Seleccione una fecha`,
    no_routes: $localize`:@@delivery.routes.no_routes:No hay entregas en esta fecha`,
    route_name: $localize`:@@delivery.routes.route_name:Nombre de la ruta`,
    truck_plate: $localize`:@@delivery.routes.truck_plate:Placa del camión`,
    driver_name: $localize`:@@delivery.routes.driver_name:Nombre del conductor`,
    route_map: $localize`:@@delivery.routes.route_map:Mapa de ruta`,
    view_map: $localize`:@@delivery.routes.view_map:Navegar ruta`
  };

  ngOnInit() {
    // Cargar las rutas para la fecha actual al iniciar
    this.loadRoutes(this.selectedDate());
  }

  // Método para cargar las rutas según la fecha seleccionada
  loadRoutes(date: Date) {
    // Formatear la fecha seleccionada para comparar
    const selectedDateStr = formatDate(date, 'yyyy-MM-dd', 'es-CO');
    
    // Obtener los pedidos desde el servicio
    this.pedidoService.listPedidos().subscribe(pedidos => { 
      // Obtener las rutas desde el servicio
      this.rutaService.listRutas().subscribe(rutas => {
        // Paso 1: Agrupar pedidos por fechaEntrega
        const pedidosPorFecha = this.agruparPedidosPorFecha(pedidos);
        
        // Paso 2 y 3: Crear objetos DeliveryRoute con máximo 3 pedidos por fecha
        const deliveryRoutes = this.crearDeliveryRoutes(pedidosPorFecha);
        
        // Paso 4: Asignar datos de Ruta a cada DeliveryRoute
        const allRoutes = this.asignarDatosRutas(deliveryRoutes, rutas);
        
        // Filtrar rutas por la fecha seleccionada
        const filteredRoutes = allRoutes.filter(route =>
          formatDate(route.fechaEntrega, 'yyyy-MM-dd', 'es-CO') === selectedDateStr
        );
        
        // Actualizar las señales
        this.routes.set(allRoutes);
        this.filteredRoutes.set(filteredRoutes);
        this.hasRoutes.set(filteredRoutes.length > 0);
      });
    });
  }
  
  // Método para agrupar pedidos por fechaEntrega
  private agruparPedidosPorFecha(pedidos: Pedido[]): { [key: string]: Pedido[] } {
    const pedidosPorFecha: { [key: string]: Pedido[] } = {};
    
    pedidos.forEach(pedido => {
      const fecha = formatDate(new Date(pedido.fechaEntrega), 'yyyy-MM-dd', 'es-CO');
      if (!pedidosPorFecha[fecha]) {
        pedidosPorFecha[fecha] = [];
      }
      pedidosPorFecha[fecha].push(pedido);
    });
    
    return pedidosPorFecha;
  }
  
  // Método para crear objetos DeliveryRoute con máximo de pedidos por fecha según MAX_PEDIDOS_POR_RUTA
  private crearDeliveryRoutes(pedidosPorFecha: { [key: string]: Pedido[] }): DeliveryRoute[] {
    const deliveryRoutes: DeliveryRoute[] = [];
    
    // Recorrer grupos de pedidos por fecha
    Object.keys(pedidosPorFecha).forEach(fecha => {
      const pedidosEnFecha = pedidosPorFecha[fecha];
      
      // Dividir pedidos en grupos según MAX_PEDIDOS_POR_RUTA
      for (let i = 0; i < pedidosEnFecha.length; i += this.MAX_PEDIDOS_POR_RUTA) {
        const pedidosGrupo = pedidosEnFecha.slice(i, i + this.MAX_PEDIDOS_POR_RUTA);
        
        // Crear arreglo de direcciones únicas de los pedidos
        const direccionesSet = new Set<string>();
        pedidosGrupo.forEach(pedido => {
          direccionesSet.add(pedido.direccion);
        });
        const direcciones = Array.from(direccionesSet);
        
        // Crear un nuevo DeliveryRoute
        const deliveryRoute: DeliveryRoute = {
          id: `route-${fecha}-${i}`,
          nombre: '', // Se completará después con los datos de la ruta
          placa: '', // Se completará después con los datos de la ruta
          conductor: '', // Se completará después con los datos de la ruta
          routeMapUrl: this.generateStaticMapUrl(direcciones),
          fechaEntrega: new Date(pedidosGrupo[0].fechaEntrega),
          direcciones: direcciones
        };
        
        deliveryRoutes.push(deliveryRoute);
      }
    });
    
    return deliveryRoutes;
  }
  
  // Método para asignar datos de rutas a los objetos DeliveryRoute
  private asignarDatosRutas(deliveryRoutes: DeliveryRoute[], rutas: Ruta[]): DeliveryRoute[] {
    if (rutas.length === 0) {
      return deliveryRoutes;
    }
    
    return deliveryRoutes.map((route, index) => {
      // Calcular el índice para asignar una ruta, ciclando si hay más deliveryRoutes que rutas
      const rutaIndex = index % rutas.length;
      const ruta = rutas[rutaIndex];
      
      return {
        ...route,
        nombre: ruta.nombre,
        placa: ruta.placa,
        conductor: ruta.conductor
      };
    });
  }

  // Método para manejar el cambio de fecha
  onDateChange(event: any) {
    this.selectedDate.set(event.value);
    this.loadRoutes(event.value);
  }

  // Método para abrir el mapa en una nueva ventana
  openMap(route: DeliveryRoute) {
    // Crear una URL para Google Maps interactivo en lugar de una imagen estática
    let mapUrl = 'https://www.google.com/maps/dir/?api=1';

    if (route.direcciones && route.direcciones.length > 0) {
      // Origen (primera parada)
      const origin = encodeURIComponent(route.direcciones[0]);
      mapUrl += `&origin=${origin}`;
      
      // Destino (última parada, si es diferente al origen)
      const destination = encodeURIComponent(route.direcciones[route.direcciones.length > 1 ? route.direcciones.length - 1 : 0]);
      mapUrl += `&destination=${destination}`;

      // Puntos intermedios (si hay más de 2 direcciones)
      if (route.direcciones.length > 2) {
        // Google Maps espera los waypoints sin el primero y el último
        const waypointsEncoded = route.direcciones
          .slice(1, -1)
          .map(wp => encodeURIComponent(wp))
          .join('|');
        mapUrl += `&waypoints=${waypointsEncoded}`;
      }
      
      // Modo de transporte
      mapUrl += '&travelmode=driving';
    } else {
      // Si no hay waypoints, abrir un mapa centrado en una ubicación predeterminada
      mapUrl = `https://www.google.com/maps/search/?api=1&query=Bogota,Colombia`;
    }
    
    window.open(mapUrl, '_blank');
  }
  
  // Método para generar la URL del mapa estático a partir de los waypoints con direcciones
  private generateStaticMapUrl(waypoints: string[], zoom: number = 12, size: string = '400x200'): string {
    if (!waypoints || waypoints.length === 0) {
      return '';
    }
    
    const center = encodeURIComponent(waypoints[0]); // Usar el primer punto como centro
    let url = `https://maps.googleapis.com/maps/api/staticmap?center=${center}&zoom=${zoom}&size=${size}`;
    
    // Añadir marcadores para cada waypoint
    const markers = waypoints.map(wp => encodeURIComponent(wp)).join('|');
    url += '&markers=color:red|' + markers;
    
    // Añadir la ruta como una línea entre los puntos
    const path = waypoints.map(wp => encodeURIComponent(wp)).join('|');
    url += '&path=color:0x0000ff|weight:5|' + path;
    
    // Cerrar el circuito volviendo al primer punto si hay más de un punto
    if (waypoints.length > 1) {
      url += '|' + encodeURIComponent(waypoints[0]);
    }
    
    // Añadir la API key
    url += `&key=${environment.googleMapsApiKey}`;
    
    return url;
  }
}
