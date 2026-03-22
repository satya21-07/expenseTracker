import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { Transaction, Category } from '../../core/models/app.models';

@Component({
  selector: 'app-transaction-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.css']
})
export class TransactionListComponent implements OnInit {
  transactions: Transaction[] = [];
  categories: Category[] = [];
  showForm = false;
  editingId: string | null = null;
  transactionForm: FormGroup;

  private apiService = inject(ApiService);
  private fb = inject(FormBuilder);

  constructor() {
    this.transactionForm = this.fb.group({
      amount: ['', [Validators.required, Validators.min(0.01)]],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      type: ['EXPENSE', Validators.required],
      categoryId: ['', Validators.required],
      description: ['']
    });
  }

  ngOnInit() {
    this.loadTransactions();
    this.loadCategories();
  }

  loadTransactions() {
    this.apiService.getTransactions({ page: 0, size: 50 }).subscribe({
      next: (res: any) => this.transactions = res.content
    });
  }

  loadCategories() {
    this.apiService.getCategories().subscribe({
      next: (res) => this.categories = res
    });
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.editingId = null;
      this.transactionForm.reset({ type: 'EXPENSE', date: new Date().toISOString().split('T')[0] });
    }
  }

  editTransaction(t: Transaction) {
    this.editingId = t.id;
    this.showForm = true;
    this.transactionForm.patchValue({
      amount: t.amount,
      date: t.date,
      type: t.type,
      categoryId: t.categoryId,
      description: t.description
    });
  }

  onSubmit() {
    if (this.transactionForm.valid) {
      const request = this.editingId
        ? this.apiService.updateTransaction(this.editingId, this.transactionForm.value)
        : this.apiService.addTransaction(this.transactionForm.value);

      request.subscribe({
        next: () => {
          this.loadTransactions();
          this.toggleForm();
        }
      });
    }
  }

  deleteTransaction(id: string) {
    if (confirm('Delete this transaction?')) {
      this.apiService.deleteTransaction(id).subscribe({
        next: () => this.loadTransactions()
      });
    }
  }
}
