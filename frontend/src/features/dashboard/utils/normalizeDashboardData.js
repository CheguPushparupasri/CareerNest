export function normalizeDashboardData(rawData) {
  if (!rawData) {
    return {
      user: null,
      statistics: [],
      careerHealth: null,
      learningProgress: [],
      applicationStatus: [],
      activityTrend: [],
      recentProjects: [],
      recentCertifications: [],
      upcomingTasks: [],
      notifications: [],
      activityFeed: [],
      quickActions: [],
    };
  }

  return {
    user: rawData.user
      ? {
          fullName: rawData.user.fullName,
          role: rawData.user.role,
          emailVerified: rawData.user.emailVerified,
          profileSetupCompleted: rawData.user.profileSetupCompleted,
        }
      : null,
    statistics: (rawData.statistics || []).map((stat) => ({
      key: stat.key,
      label: stat.label,
      value: stat.value ?? 0,
      unit: stat.suffix || '',
      description: stat.trend || 'No data yet',
      color: stat.tone || 'blue',
    })),
    careerHealth: rawData.careerHealth
      ? {
          overall: rawData.careerHealth.overallScore ?? rawData.careerHealth.overall ?? 0,
          resumeScore: rawData.careerHealth.resumeScore ?? 0,
          skillScore: rawData.careerHealth.skillScore ?? 0,
          placementReadiness: rawData.careerHealth.placementReadiness ?? 0,
          learningProgress: rawData.careerHealth.learningProgress ?? 0,
          codingConsistency: rawData.careerHealth.codingConsistency ?? 0,
          projectStrength: rawData.careerHealth.projectStrength ?? 0,
          recommendations: rawData.careerHealth.recommendations || [],
        }
      : null,
    learningProgress: (rawData.learningProgress || []).map((item) => ({
      skill: item.track || item.skill || 'Untitled',
      progress: item.progress ?? 0,
      status: item.status || 'Not started',
    })),
    applicationStatus: (rawData.applicationStatus || []).map((item) => ({
      label: item.label || 'Unknown',
      value: item.value ?? 0,
    })),
    activityTrend: (rawData.activityTrend || []).map((item) => ({
      label: item.label || 'Unknown',
      value: item.value ?? 0,
    })),
    recentProjects: rawData.recentProjects || [],
    recentCertifications: rawData.recentCertifications || [],
    upcomingTasks: rawData.upcomingTasks || [],
    notifications: (rawData.notifications || []).map((item, index) => ({
      id: item.id || index,
      title: item.title || 'Notification',
      message: item.message || '',
      type: item.priority || item.type || 'INFO',
      isRead: item.read ?? item.isRead ?? false,
      timestamp: item.createdAt || item.timestamp || null,
    })),
    activityFeed: (rawData.activityFeed || []).map((item, index) => ({
      id: index,
      title: item.title || item.type || 'Activity',
      description: item.description || '',
      timestamp: item.createdAt || item.timestamp || null,
    })),
    quickActions: (rawData.quickActions || []).map((item, index) => ({
      id: index,
      title: item.label || item.title || 'Action',
      description: item.description || '',
      link: item.route || item.link || '#',
      icon: item.icon || 'zap',
    })),
  };
}
