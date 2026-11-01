import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, FileText, Star, Edit, Trash2, Eye, Award } from 'lucide-react';
import { resumeService } from '../services/resumeService.js';
import { Link } from 'react-router-dom';

export function ResumesPage() {
  const [resumes, setResumes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadResumes();
  }, []);

  const loadResumes = async () => {
    try {
      setLoading(true);
      const data = await resumeService.getAll();
      setResumes(data);
    } catch (error) {
      console.error('Failed to load resumes:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, title) => {
    if (!confirm(`Are you sure you want to delete "${title}"?`)) return;
    try {
      await resumeService.delete(id);
      loadResumes();
    } catch (error) {
      console.error('Failed to delete resume:', error);
    }
  };

  const handleSetPrimary = async (id) => {
    try {
      await resumeService.setPrimary(id);
      loadResumes();
    } catch (error) {
      console.error('Failed to set primary resume:', error);
    }
  };

  const getAtsScoreColor = (score) => {
    if (score >= 80) return 'text-green-600 bg-green-50 dark:bg-green-950/30';
    if (score >= 60) return 'text-amber-600 bg-amber-50 dark:bg-amber-950/30';
    return 'text-red-600 bg-red-50 dark:bg-red-950/30';
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-3xl font-bold">Resumes</h1>
          <button className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700">
            <Plus size={18} />
            Create Resume
          </button>
        </div>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-48 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />
          ))}
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-3xl font-bold">Resumes</h1>
        <Link
          to="/resumes/new"
          className="flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
        >
          <Plus size={18} />
          Create Resume
        </Link>
      </div>

      {resumes.length === 0 ? (
        <div className="rounded-xl border border-slate-200 bg-white p-12 text-center shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <div className="mx-auto mb-4 rounded-full bg-slate-100 p-4 dark:bg-slate-800">
            <FileText className="text-slate-400" size={32} />
          </div>
          <h3 className="mb-2 text-lg font-semibold text-slate-950 dark:text-slate-50">No resumes yet</h3>
          <p className="mb-4 text-slate-500">Create your first ATS-friendly resume to start building your career portfolio.</p>
          <Link
            to="/resumes/new"
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
          >
            <Plus size={18} />
            Create Your First Resume
          </Link>
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {resumes.map((resume, index) => (
            <motion.div
              key={resume.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              className="group relative rounded-xl border border-slate-200 bg-white p-5 shadow-soft transition-all hover:shadow-md dark:border-slate-800 dark:bg-slate-900"
            >
              {resume.isPrimary && (
                <div className="absolute right-3 top-3">
                  <Star className="fill-amber-400 text-amber-400" size={18} />
                </div>
              )}
              
              <div className="mb-4">
                <h3 className="text-lg font-semibold text-slate-950 dark:text-slate-50 line-clamp-1">
                  {resume.title}
                </h3>
                <p className="text-sm text-slate-500">{resume.template}</p>
              </div>

              <div className="mb-4 flex items-center gap-3">
                <div className={`flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-medium ${getAtsScoreColor(resume.atsScore || 0)}`}>
                  <Award size={14} />
                  ATS Score: {resume.atsScore || 0}/100
                </div>
                {resume.isPrimary && (
                  <span className="rounded-full bg-blue-100 px-3 py-1 text-xs font-medium text-blue-700 dark:bg-blue-950/30 dark:text-blue-300">
                    Primary
                  </span>
                )}
              </div>

              <div className="flex items-center gap-2">
                <Link
                  to={`/resumes/${resume.id}`}
                  className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                >
                  <Eye size={16} />
                </Link>
                <Link
                  to={`/resumes/${resume.id}/edit`}
                  className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                >
                  <Edit size={16} />
                </Link>
                <button
                  onClick={() => handleSetPrimary(resume.id)}
                  className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                  title="Set as primary"
                >
                  <Star size={16} className={resume.isPrimary ? 'fill-amber-400 text-amber-400' : ''} />
                </button>
                <div className="flex-1" />
                <button
                  onClick={() => handleDelete(resume.id, resume.title)}
                  className="rounded-lg bg-red-50 p-2 text-red-600 transition-colors hover:bg-red-100 dark:bg-red-950/30 dark:text-red-400 dark:hover:bg-red-950/50"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </section>
  );
}
