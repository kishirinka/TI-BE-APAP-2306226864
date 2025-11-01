export interface CommonResponseInterface<T> {
  success: boolean;
  status: number;
  message: string;
  timestamp: string;
  data: T;
}

