import { axiosClient } from '../../../lib/axiosClient.js';

export const certificationService = {
  getAll: async (params = {}) => (await axiosClient.get('/certifications', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/certifications/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/certifications', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/certifications/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/certifications/${id}`),
  search: async (query, params = {}) => (await axiosClient.get('/certifications/search', { params: { ...params, query } })).data.data,
  getByCategory: async (category, params = {}) => (await axiosClient.get(`/certifications/category/${category}`, { params })).data.data,
  count: async () => (await axiosClient.get('/certifications/count')).data.data,
};
