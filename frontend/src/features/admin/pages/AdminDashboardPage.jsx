import { useEffect, useState } from 'react';
import { Activity, Award, BookOpen, BriefcaseBusiness, Code, FileText, Users } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

const metrics = [
  { key: 'totalUsers', label: 'Total users', icon: Users },
  { key: 'activeUsers', label: 'Non-deleted users', icon: Activity },
  { key: 'totalProjects', label: 'Projects', icon: BriefcaseBusiness },
  { key: 'totalResumes', label: 'Resumes', icon: FileText },
  { key: 'totalCertifications', label: 'Certifications', icon: Award },
  { key: 'totalApplications', label: 'Applications', icon: BriefcaseBusiness },
  { key: 'totalLearningCourses', label: 'Learning courses', icon: BookOpen },
  { key: 'totalCodingProfiles', label: 'Coding profiles', icon: Code },
];

export function AdminDashboardPage() {
  const [overview, setOverview] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getOverview()
      .then(setOverview)
      .catch((requestError) => setError(requestError.response?.data?.message || 'Unable to load platform overview.'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className="space-y-6">
      <header>
        <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Platform overview</h1>
        <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Current aggregate counts from CareerNest.</p>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4" aria-label="Loading overview">
          {metrics.slice(0, 4).map((metric) => <div key={metric.key} className="h-28 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />)}
        </div>
      ) : overview ? (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {metrics.map(({ key, label, icon: Icon }) => (
            <Panel key={key} className="p-5">
              <div className="flex items-center justify-between gap-3">
                <span className="text-sm text-slate-600 dark:text-slate-400">{label}</span>
                <Icon size={18} className="text-secondary-600 dark:text-secondary-400" aria-hidden="true" />
              </div>
              <p className="mt-3 text-3xl font-semibold tabular-nums text-slate-950 dark:text-slate-50">
                {Number(overview[key] ?? 0).toLocaleString()}
              </p>
            </Panel>
          ))}
        </div>
      ) : null}
    </section>
  );
}