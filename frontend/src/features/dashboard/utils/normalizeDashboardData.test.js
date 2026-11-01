import test from 'node:test';
import assert from 'node:assert/strict';
import { normalizeDashboardData } from './normalizeDashboardData.js';

test('normalizes dashboard payload from the backend contract', () => {
  const raw = {
    user: {
      fullName: 'Ada Lovelace',
      role: 'USER',
      emailVerified: true,
      profileSetupCompleted: true,
    },
    statistics: [
      { key: 'profileCompletion', label: 'Profile Completion', value: 85, suffix: '%', trend: 'Strong', tone: 'blue' },
    ],
    careerHealth: {
      overallScore: 78,
      resumeScore: 60,
      skillScore: 70,
      placementReadiness: 80,
      learningProgress: 75,
      codingConsistency: 65,
      projectStrength: 55,
      recommendations: ['Keep going'],
    },
    learningProgress: [{ track: 'Java', progress: 40, status: 'In progress' }],
    applicationStatus: [{ label: 'Applied', value: 2 }],
    activityTrend: [{ label: 'Mon', value: 3 }],
    notifications: [{ title: 'Profile reminder', message: 'Complete setup', priority: 'HIGH', read: false, createdAt: '2024-01-01T00:00:00Z' }],
    activityFeed: [{ type: 'LOGIN', title: 'Signed in', description: 'Welcome back', createdAt: '2024-01-01T00:00:00Z' }],
    quickActions: [{ label: 'Complete Profile', description: 'Improve dashboard personalization', route: '/profile-setup', icon: 'user' }],
  };

  const normalized = normalizeDashboardData(raw);

  assert.equal(normalized.user.fullName, 'Ada Lovelace');
  assert.equal(normalized.statistics[0].key, 'profileCompletion');
  assert.equal(normalized.careerHealth.overall, 78);
  assert.equal(normalized.learningProgress[0].skill, 'Java');
  assert.equal(normalized.notifications[0].type, 'HIGH');
  assert.equal(normalized.activityFeed[0].timestamp, '2024-01-01T00:00:00Z');
  assert.equal(normalized.quickActions[0].title, 'Complete Profile');
  assert.equal(normalized.quickActions[0].link, '/profile-setup');
});
