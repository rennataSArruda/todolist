export interface EmailConfig {
  id?: number;
  host: string;
  port: number;
  username: string;
  password: string;
  fromAddress: string;
  fromName: string;
  auth: boolean;
  startTls: boolean;
  ssl: boolean;
  ativo: boolean;
  createdAt?: string;
  updatedAt?: string;
}
