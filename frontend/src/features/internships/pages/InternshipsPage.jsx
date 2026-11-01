import { useEffect, useMemo, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, Briefcase, Trash2, Edit, X } from 'lucide-react';
import { internshipService } from '../services/internshipService.js';

export function InternshipsPage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [form, setForm] = useState({ companyName: '', position: '', roleType: '', location: '', status: 'APPLIED', source: '', notes: '', applicationDate: '', deadline: '', interviewStage: 'APPLIED' });

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await internshipService.getAll({ page: 0, size: 20 });
      setItems(data.content || []);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load applications.');
    } finally {
      setLoading(false);
    }
  };

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const haystack = `${item.companyName} ${item.position} ${item.status}`.toLowerCase();
      const matchesQuery = !query || haystack.includes(query.toLowerCase());
      const matchesStatus = !status || item.status === status;
      return matchesQuery && matchesStatus;
    });
  }, [items, query, status]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    const toInstant = (value) => value ? `${value}T00:00:00Z` : '';
    const payload = { ...form, applicationDate: toInstant(form.applicationDate), deadline: toInstant(form.deadline) };
    try {
      if (editingId) await internshipService.update(editingId, payload);
      else await internshipService.create(payload);
      setMessage(editingId ? 'Application updated.' : 'Application added.');
      setEditingId(null);
      setForm({ companyName: '', position: '', roleType: '', location: '', status: 'APPLIED', source: '', notes: '', applicationDate: '', deadline: '', interviewStage: 'APPLIED' });
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to save application.');
    }
  };

  const startEdit = (item) => {
    const toDate = (value) => value ? new Date(value).toISOString().slice(0, 10) : '';
    setEditingId(item.id);
    setForm({ companyName: item.companyName, position: item.position, roleType: item.roleType || '',
      location: item.location || '', status: item.status || 'APPLIED', source: item.source || '', notes: item.notes || '',
      applicationDate: toDate(item.applicationDate), deadline: toDate(item.deadline), interviewStage: item.interviewStage || '' });
    setError('');
    setMessage('');
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this application?')) return;
    try {
      await internshipService.delete(id);
      if (editingId === id) setEditingId(null);
      setMessage('Application deleted.');
      await loadData();
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to delete application.');
    }
  };

  return (
    <section className="space-y-6">
      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        {error && <p role="alert" className="mb-3 text-sm text-red-600">{error}</p>}
        {message && <p role="status" className="mb-3 text-sm text-green-700">{message}</p>}
        <h2 className="mb-4 text-xl font-semibold">{editingId ? 'Edit application' : 'Track internship & job applications'}</h2>
        <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Company" value={form.companyName} onChange={(e) => setForm({ ...form, companyName: e.target.value })} required />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Position" value={form.position} onChange={(e) => setForm({ ...form, position: e.target.value })} required />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Role type" value={form.roleType} onChange={(e) => setForm({ ...form, roleType: e.target.value })} />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Location" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
          <select className="rounded-lg border border-slate-300 px-3 py-2" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
            <option value="SAVED">Saved</option>
            <option value="APPLIED">Applied</option>
            <option value="ASSESSMENT">Assessment</option>
            <option value="SCREENING">Screening</option>
            <option value="INTERVIEW">Interview</option>
            <option value="OFFER">Offer</option>
            <option value="SELECTED">Selected</option>
            <option value="REJECTED">Rejected</option>
          </select>
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Source or application URL" value={form.source} onChange={(e) => setForm({ ...form, source: e.target.value })} />
          <input type="date" className="rounded-lg border border-slate-300 px-3 py-2" value={form.applicationDate} onChange={(e) => setForm({ ...form, applicationDate: e.target.value })} />
          <input type="date" className="rounded-lg border border-slate-300 px-3 py-2" value={form.deadline} onChange={(e) => setForm({ ...form, deadline: e.target.value })} />
          <input className="rounded-lg border border-slate-300 px-3 py-2" placeholder="Interview stage" value={form.interviewStage} onChange={(e) => setForm({ ...form, interviewStage: e.target.value })} />
          <textarea className="rounded-lg border border-slate-300 px-3 py-2 md:col-span-2 xl:col-span-3" placeholder="Notes" value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
          <div className="md:col-span-2 xl:col-span-3 flex justify-end">
            <button type="submit" className="flex items-center gap-2 rounded-lg bg-brand-600 px-4 py-2 text-white">
              {editingId ? <Edit size={16} /> : <Plus size={16} />} {editingId ? 'Save application' : 'Add Application'}
            </button>
          </div>
          {editingId && <button type="button" className="md:col-span-2 xl:col-span-3 inline-flex items-center gap-2 justify-self-end text-sm text-slate-600" onClick={() => { setEditingId(null); setForm({ companyName: '', position: '', roleType: '', location: '', status: 'APPLIED', source: '', notes: '', applicationDate: '', deadline: '', interviewStage: 'APPLIED' }); }}><X size={15} /> Cancel edit</button>}
        </form>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-soft dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-4 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex gap-2">
            <div className="flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2">
              <Search size={16} />
              <input className="bg-transparent outline-none" placeholder="Search" value={query} onChange={(e) => setQuery(e.target.value)} />
            </div>
            <select className="rounded-lg border border-slate-300 px-3 py-2" value={status} onChange={(e) => setStatus(e.target.value)}>
              <option value="">All statuses</option>
              <option value="APPLIED">Applied</option>
              <option value="SCREENING">Screening</option>
              <option value="INTERVIEW">Interview</option>
              <option value="OFFER">Offer</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>
        </div>

        {loading ? (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{[1,2,3].map((i)=><div key={i} className="h-36 animate-pulse rounded-xl bg-slate-200" />)}</div>
        ) : filteredItems.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">No applications found.</div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {filteredItems.map((item) => (
              <motion.div key={item.id} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="rounded-xl border border-slate-200 p-4 dark:border-slate-800">
                <div className="mb-3 flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-semibold">{item.companyName}</h3>
                    <p className="text-sm text-slate-500">{item.position}</p>
                  </div>
                  <span className="rounded-full bg-brand-50 px-2 py-1 text-xs text-brand-700">{item.status}</span>
                </div>
                <p className="mb-3 text-sm text-slate-600">{item.location || 'Location not set'}</p>
                {item.deadline && <p className="text-xs text-slate-500">Deadline: {new Date(item.deadline).toLocaleDateString()}</p>}
                <p className="text-sm text-slate-500">{item.notes || 'No notes yet.'}</p>
                <div className="mt-4 flex justify-end gap-2">
                  <button type="button" className="rounded-lg bg-slate-100 p-2" aria-label={`Edit ${item.position} at ${item.companyName}`} onClick={() => startEdit(item)}><Edit size={16} /></button>
                  <button className="rounded-lg bg-slate-100 p-2" onClick={() => handleDelete(item.id)}><Trash2 size={16} /></button>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
