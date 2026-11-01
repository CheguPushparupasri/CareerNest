import { Settings } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function AdminSettingsPage() {
  return (
    <section className="space-y-6">
      <header>
        <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Settings</h1>
        <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">Platform configuration status.</p>
      </header>
      <Panel className="space-y-4 p-5">
        <div className="flex items-center gap-3">
          <Settings size={19} className="text-slate-500" aria-hidden="true" />
          <h2 className="font-medium text-slate-950 dark:text-slate-50">Configuration management</h2>
        </div>
        <Alert type="info">The backend does not currently expose an admin settings API. No editable settings are available here.</Alert>
      </Panel>
    </section>
  );
}