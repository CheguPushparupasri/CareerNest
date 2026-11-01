import { axiosClient } from '../../../lib/axiosClient.js';

export const dashboardService = {
  getSummary: async () => (await axiosClient.get('/dashboard/summary')).data.data,
};
