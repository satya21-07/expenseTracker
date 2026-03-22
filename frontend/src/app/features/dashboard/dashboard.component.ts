import { Component, OnInit, ElementRef, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../core/services/api.service';
import { DashboardStats } from '../../core/models/app.models';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  alerts: any[] = [];
  today = new Date();
  activeChart: 'trend' | 'comparison' | 'categories' = 'trend';

  getToday() {
    return new Date();
  }

  @ViewChild('mainChart') mainChartRef!: ElementRef;

  private chart: any;

  private apiService = inject(ApiService);

  ngOnInit() {
    this.loadStats();
    this.apiService.getAlerts().subscribe({
      next: (data) => this.alerts = data
    });
  }

  loadStats(startDate?: string, endDate?: string) {
    this.apiService.getDashboardStats(startDate, endDate).subscribe({
      next: (data) => {
        this.stats = data;
        setTimeout(() => this.initCharts(), 100);
      }
    });
  }

  setChart(type: 'trend' | 'comparison' | 'categories') {
    this.activeChart = type;
    setTimeout(() => this.initCharts(), 50);
  }

  onDateChange(event: any) {
    const dateInput = event.target.value;
    if (dateInput) {
      const [year, month, day] = dateInput.split('-').map(Number);
      this.today = new Date(year, month - 1, day);
      this.loadStats(dateInput, dateInput);
    }
  }

  resetToToday() {
    this.today = new Date();
    this.loadStats();
  }

  isFilterActive(): boolean {
    const today = new Date();
    return this.today.toDateString() !== today.toDateString();
  }

  initCharts() {
    if (!this.stats || !this.mainChartRef) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = this.mainChartRef.nativeElement.getContext('2d');

    if (this.activeChart === 'trend') {
      this.renderTrendChart(ctx);
    } else if (this.activeChart === 'comparison') {
      this.renderComparisonChart(ctx);
    } else {
      this.renderCategoryChart(ctx);
    }
  }

  private renderTrendChart(ctx: any) {
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: Object.keys(this.stats!.monthlyExpenseTrend),
        datasets: [{
          label: 'Expenses',
          data: Object.values(this.stats!.monthlyExpenseTrend),
          borderColor: '#6366f1',
          backgroundColor: 'rgba(99, 102, 241, 0.1)',
          fill: true,
          tension: 0.4
        }]
      },
      options: { responsive: true, maintainAspectRatio: false }
    });
  }

  private renderComparisonChart(ctx: any) {
    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Current Period'],
        datasets: [
          {
            label: 'Income',
            data: [this.stats!.totalIncome],
            backgroundColor: '#10b981',
            borderRadius: 8
          },
          {
            label: 'Expenses',
            data: [this.stats!.totalExpense],
            backgroundColor: '#ef4444',
            borderRadius: 8
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  private renderCategoryChart(ctx: any) {
    this.chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: Object.keys(this.stats!.categoryWiseExpense),
        datasets: [{
          data: Object.values(this.stats!.categoryWiseExpense),
          backgroundColor: ['#6366f1', '#ec4899', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444'],
          hoverOffset: 20
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { position: 'right' } }
      }
    });
  }
}
