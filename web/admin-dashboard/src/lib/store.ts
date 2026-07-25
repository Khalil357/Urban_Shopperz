import { create } from 'zustand';
import { api } from './api';
import { DashboardMetrics, Zone } from './types';

interface AppState {
  token: string | null;
  userName: string | null;
  userRole: string | null;
  metrics: DashboardMetrics | null;
  zones: Zone[];
  disputes: any[];
  loading: boolean;
  error: string | null;

  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  fetchMetrics: () => Promise<void>;
  fetchZones: () => Promise<void>;
  fetchDisputes: () => Promise<void>;
}

export const useStore = create<AppState>((set, get) => ({
  token: null,
  userName: null,
  userRole: null,
  metrics: null,
  zones: [],
  disputes: [],
  loading: false,
  error: null,

  login: async (username, password) => {
    set({ loading: true, error: null });
    try {
      const res = await api.login(username, password);
      set({
        token: res.token,
        userName: res.name,
        userRole: res.role,
        loading: false,
      });
      // Load initial data
      get().fetchMetrics();
      get().fetchZones();
      get().fetchDisputes();
      return true;
    } catch (e: any) {
      set({ error: e.message, loading: false });
      return false;
    }
  },

  logout: () => set({ token: null, userName: null, userRole: null, metrics: null }),

  fetchMetrics: async () => {
    const token = get().token;
    if (!token) return;
    try {
      const metrics = await api.getMetrics(token);
      set({ metrics });
    } catch (e: any) {
      set({ error: e.message });
    }
  },

  fetchZones: async () => {
    const token = get().token;
    if (!token) return;
    try {
      const zones = await api.getZones(token);
      set({ zones });
    } catch (e: any) {
      set({ error: e.message });
    }
  },

  fetchDisputes: async () => {
    const token = get().token;
    if (!token) return;
    try {
      const disputes = await api.getDisputeQueue(token);
      set({ disputes });
    } catch (e: any) {
      set({ error: e.message });
    }
  },
}));
