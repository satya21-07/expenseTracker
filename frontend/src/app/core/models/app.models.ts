export interface User {
  id: string;
  name: string;
  email: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  message: string;
}

export interface Transaction {
  id: string;
  amount: number;
  description: string;
  date: string;
  categoryId: string;
  categoryName: string;
  type: 'INCOME' | 'EXPENSE' | 'LENT' | 'BORROWED';
}

export interface Category {
  id: string;
  name: string;
  color: string;
  predefined: boolean;
}

export interface Budget {
  id?: string;
  categoryId: string;
  categoryName?: string;
  amount: number;
  warningThreshold: number;
}

export interface Alert {
  category: string;
  maxLimit: number;
  spent: number;
  percentageUsed: number;
  status: 'SAFE' | 'WARNING' | 'EXCEEDED';
}

export interface DashboardStats {
  totalIncome: number;
  totalExpense: number;
  totalSavings: number;
  totalLent: number;
  totalBorrowed: number;
  monthlyExpenseTrend: { [key: string]: number };
  categoryWiseExpense: { [key: string]: number };
}
