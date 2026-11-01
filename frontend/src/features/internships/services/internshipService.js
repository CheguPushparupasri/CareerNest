import { axiosClient } from '../../../lib/axiosClient.js';

export const internshipService = {
  getAll: async (params = {}) => (await axiosClient.get('/internships', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/internships/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/internships', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/internships/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/internships/${id}`),
  search: async (query, params = {}) => (await axiosClient.get('/internships/search', { params: { ...params, query } })).data.data,
  getByStatus: async (status, params = {}) => (await axiosClient.get(`/internships/status/${status}`, { params })).data.data,
  count: async () => (await axiosClient.get('/internships/count')).data.data,
};
