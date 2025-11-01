export enum ServiceEnum {
  ACCOMMODATION = 'ACCOMMODATION',
  FLIGHT = 'FLIGHT',
  PACKAGE = 'PACKAGE',
  RENTALS = 'RENTALS'
}

export const ServiceLabels: Record<ServiceEnum, string> = {
  [ServiceEnum.ACCOMMODATION]: 'Accommodation',
  [ServiceEnum.FLIGHT]: 'Flight',
  [ServiceEnum.PACKAGE]: 'Package',
  [ServiceEnum.RENTALS]: 'Rentals'
}
