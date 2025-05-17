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
    // Normalmente aquí se haría una llamada a un servicio para obtener los datos
    // Por ahora usaremos datos de ejemplo
    
    // Formatear la fecha seleccionada para comparar
    const selectedDateStr = formatDate(date, 'yyyy-MM-dd', 'es-CO');
    const currentDateStr = formatDate(new Date(), 'yyyy-MM-dd', 'es-CO');
    
    // Definición de waypoints usando direcciones en lugar de coordenadas
    const centroWaypoints = [
      'Carrera 7 # 32-16, Bogotá, Colombia', 
      'Plaza de Bolívar, Bogotá, Colombia',
      'Museo Nacional, Bogotá, Colombia',
      'Parque de la 93, Bogotá, Colombia'
    ];
    
    const norteWaypoints = [
      'Centro Comercial Santafé, Bogotá, Colombia',
      'Parque Simón Bolívar, Bogotá, Colombia',
      'Universidad Nacional, Bogotá, Colombia',
      'Centro Andino, Bogotá, Colombia'
    ];
    
    const surWaypoints = [
      'Centro Comercial El Tunal, Bogotá, Colombia',
      'Biblioteca El Tintal, Bogotá, Colombia',
      'Portal Sur Transmilenio, Bogotá, Colombia',
      'Centro Comercial Ciudad Tunal, Bogotá, Colombia'
    ];
    
    // Datos de ejemplo - En una aplicación real esto vendría de un servicio
    const allRoutes: DeliveryRoute[] = [
      {
        id: '1',
        name: 'Ruta Centro',
        truckPlate: 'XYZ-123',
        driverName: 'Carlos Ramírez',
        routeMapUrl: this.generateStaticMapUrl(centroWaypoints),
        date: new Date(),
        waypoints: centroWaypoints
      },
      {
        id: '2',
        name: 'Ruta Norte',
        truckPlate: 'ABC-456',
        driverName: 'Juan Pérez',
        routeMapUrl: this.generateStaticMapUrl(norteWaypoints),
        date: new Date(),
        waypoints: norteWaypoints
      },
      {
        id: '3',
        name: 'Ruta Sur',
        truckPlate: 'DEF-789',
        driverName: 'Luis Torres',
        routeMapUrl: this.generateStaticMapUrl(surWaypoints),
        date: new Date(new Date().setDate(new Date().getDate() + 1)),
        waypoints: surWaypoints
      }
    ];
    
    // Filtrar rutas por la fecha seleccionada
    const filteredRoutes = allRoutes.filter(route => 
      formatDate(route.date, 'yyyy-MM-dd', 'es-CO') === selectedDateStr
    );
    
    // Actualizar las señales
    this.routes.set(allRoutes);
    this.filteredRoutes.set(filteredRoutes);
    this.hasRoutes.set(filteredRoutes.length > 0);
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
    
    if (route.waypoints && route.waypoints.length > 0) {
      // Origen (primera parada)
      const origin = encodeURIComponent(route.waypoints[0]);
      mapUrl += `&origin=${origin}`;
      
      // Destino (última parada, si es diferente al origen)
      const destination = encodeURIComponent(route.waypoints[route.waypoints.length > 1 ? route.waypoints.length - 1 : 0]);
      mapUrl += `&destination=${destination}`;
      
      // Puntos intermedios (si hay más de 2 waypoints)
      if (route.waypoints.length > 2) {
        // Google Maps espera los waypoints sin el primero y el último
        const waypointsEncoded = route.waypoints
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
