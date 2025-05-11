export interface DeliveryRoute {
  id: string;
  name: string;
  truckPlate: string;
  driverName: string;
  routeMapUrl: string;
  date: Date;
  waypoints?: string[];
}
