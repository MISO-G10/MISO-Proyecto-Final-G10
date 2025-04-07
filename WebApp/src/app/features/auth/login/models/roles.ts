// types/roles.ts
export const ROLES = {
    ADMINISTRADOR: 'ADMINISTRADOR',
    TENDERO: 'TENDERO',
    VENDEDOR: 'VENDEDOR',
    LOGISTICA: 'LOGISTICA',
    DIRECTOR_VENTAS: 'DIRECTOR_VENTAS'
  } as const;
  
  export type UserRole = keyof typeof ROLES;