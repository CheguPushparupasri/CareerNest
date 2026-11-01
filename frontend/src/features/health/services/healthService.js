import { axiosClient } from '../../../lib/axiosClient.js';

export const healthService = {
  getHealth: async () => (await axiosClient.get('/health-score')).data.data,
};
