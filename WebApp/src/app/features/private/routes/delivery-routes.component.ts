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
import { DeliveryRoute } from './models/delivery-route.model';
import { formatDate } from '@angular/common';
import { environment } from '../../../../environment/environment';

@Component({
  selector: 'app-delivery-routes',
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
  templateUrl: './delivery-routes.component.html',
  styleUrls: ['./delivery-routes.component.scss']
})
export class DeliveryRoutesComponent implements OnInit {
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
    
    // Definición de waypoints para las rutas
    const centroWaypoints = [
      '4.6097,-74.0817',
      '4.6260,-74.0686',
      '4.6019,-74.0947',
      '4.5981,-74.0758'
    ];
    
    const norteWaypoints = [
      '4.7500,-74.0500',
      '4.7681,-74.0430',
      '4.7321,-74.0692',
      '4.7612,-74.0348'
    ];
    
    const surWaypoints = [
      '4.5700,-74.1000',
      '4.5518,-74.1115',
      '4.5902,-74.1213',
      '4.5601,-74.0831'
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
      const origin = route.waypoints[0];
      mapUrl += `&origin=${origin}`;
      
      // Destino (última parada, si es diferente al origen)
      const destination = route.waypoints[route.waypoints.length > 1 ? route.waypoints.length - 1 : 0];
      mapUrl += `&destination=${destination}`;
      
      // Puntos intermedios (si hay más de 2 waypoints)
      if (route.waypoints.length > 2) {
        // Google Maps espera los waypoints sin el primero y el último
        const waypoints = route.waypoints.slice(1, -1).join('|');
        mapUrl += `&waypoints=${waypoints}`;
      }
      
      // Modo de transporte
      mapUrl += '&travelmode=driving';
    } else {
      // Si no hay waypoints, simplemente abrir el mapa centrado en las coordenadas
      const center = this.getCoordinatesFromUrl(route.routeMapUrl);
      mapUrl = `https://www.google.com/maps/search/?api=1&query=${center}`;
    }
    
    window.open(mapUrl, '_blank');
  }

  // Método auxiliar para extraer las coordenadas de la URL
  private getCoordinatesFromUrl(url: string): string {
    const regex = /center=([^&]+)/;
    const match = url.match(regex);
    return match ? match[1] : '4.6097,-74.0817'; // Coordenadas predeterminadas si no se encuentra
  }
  
  // Método para generar la URL del mapa estático a partir de los waypoints
  private generateStaticMapUrl(waypoints: string[], zoom: number = 12, size: string = '400x200'): string {
    if (!waypoints || waypoints.length === 0) {
      return '';
    }
    
    const center = waypoints[0]; // Usar el primer punto como centro
    let url = `https://maps.googleapis.com/maps/api/staticmap?center=${center}&zoom=${zoom}&size=${size}`;
    
    // Añadir marcadores para cada waypoint
    url += '&markers=color:red|' + waypoints.join('|');
    
    // Añadir la ruta como una línea entre los puntos
    url += '&path=color:0x0000ff|weight:5|' + waypoints.join('|');
    
    // Cerrar el circuito volviendo al primer punto
    if (waypoints.length > 1) {
      url += '|' + waypoints[0];
    }
    
    // Añadir la API key
    url += `&key=${environment.googleMapsApiKey}`;
    
    return url;
  }
}
