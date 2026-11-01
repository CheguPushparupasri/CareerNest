import { useEffect, useState } from 'react';
import { Download, FileText } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { adminService } from '../services/adminService.js';

const reportFields = [
  ['Total users', 'totalUsers'],
  ['Active users', 'activeUsers'],
  ['Projects', 'totalProjects'],
  ['Resumes', 'totalResumes'],
  ['Certifications', 'totalCertifications'],
  ['Applications', 'totalApplications'],
  ['Learning courses', 'totalLearningCourses'],
  ['Coding profiles', 'totalCodingProfiles'],
];

export function AdminReportsPage() {
  const [overview, setOverview] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getOverview()
      .then(setOverview)
      .catch((requestError) => setError(requestError.response?.data?.message || 'Unable to load report data.'))
      .finally(() => setLoading(false));
  }, []);

  const downloadReport = () => {
    const csv = [
      ['Metric', 'Value'],
      ...reportFields.map(([label, key]) => [label, String(overview[key] ?? 0)]),
    ].map((row) => row.map((value) => `"${value.replaceAll('"', '""')}"`).join(',')).join('\n');
    const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = 'careernest-platform-report.csv';
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <section className="space-y-6">
      <header className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Reports</h1>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Export a current snapshot of the totals exposed by the backend.</p>
        </div>
        <Button type="button" disabled={!overview || loading} onClick={downloadReport}>
          <Download size={17} className="mr-2" /> Export CSV
        </Button>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      <Panel className="overflow-hidden p-0">
        {loading ? <p className="p-6 text-sm text-slate-600 dark:text-slate-400">Loading report data...</p> : overview ? (
          <>
            <div className="flex items-center gap-3 border-b border-slate-200 p-5 dark:border-slate-800">
              <FileText size={19} className="text-secondary-600" aria-hidden="true" />
              <div>
                <h2 className="font-medium text-slate-950 dark:text-slate-50">Platform totals</h2>
                <p className="text-xs text-slate-500 dark:text-slate-400">Values are fetched from the admin overview endpoint.</p>
              </div>
            </div>
            <dl className="divide-y divide-slate-200 dark:divide-slate-800">
              {reportFields.map(([label, key]) => (
                <div key={key} className="flex items-center justify-between gap-4 px-5 py-3 text-sm">
                  <dt className="text-slate-600 dark:text-slate-400">{label}</dt>
                  <dd className="font-semibold tabular-nums text-slate-950 dark:text-slate-50">{Number(overview[key] ?? 0).toLocaleString()}</dd>
                </div>
              ))}
            </dl>
          </>
        ) : null}
      </Panel>
    </section>
  );
}