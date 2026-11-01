import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { ArrowLeft, Edit, Trash2, Star, Printer, Award, History } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';
import { resumeService } from '../services/resumeService.js';

function resumeDetails(content) {
  if (!content) return { personal: {}, summary: '', education: '', skills: '', experience: '', projects: '', certifications: '' };
  try {
    const parsed = JSON.parse(content);
    if (parsed?.format === 'careernest-resume-v1') return parsed;
  } catch {
    return { personal: {}, summary: content, education: '', skills: '', experience: '', projects: '', certifications: '' };
  }
  return { personal: {}, summary: content, education: '', skills: '', experience: '', projects: '', certifications: '' };
}

function ResumeSection({ title, content }) {
  if (!content?.trim()) return null;
  return (
    <section className="mt-5 break-inside-avoid">
      <h3 className="border-b border-slate-300 pb-1 text-sm font-bold uppercase tracking-wide text-slate-800">{title}</h3>
      <div className="mt-2 space-y-1 whitespace-pre-wrap text-sm leading-6 text-slate-700">{content}</div>
    </section>
  );
}

export function ResumeDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [resume, setResume] = useState(null);
  const [versions, setVersions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showVersions, setShowVersions] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadResume();
  }, [id]);

  const loadResume = async () => {
    try {
      const data = await resumeService.getById(id);
      setResume(data);
      const versionData = await resumeService.getVersions(id);
      setVersions(versionData);
    } catch (error) {
      setError(error.response?.data?.message || 'Unable to load resume.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm(`Are you sure you want to delete "${resume.title}"?`)) return;
    try {
      await resumeService.delete(id);
      navigate('/resumes');
    } catch (error) {
      console.error('Failed to delete resume:', error);
    }
  };

  const handleSetPrimary = async () => {
    try {
      await resumeService.setPrimary(id);
      loadResume();
    } catch (error) {
      console.error('Failed to set primary resume:', error);
    }
  };

  const getAtsScoreColor = (score) => {
    if (score >= 80) return 'text-green-600 bg-green-50 dark:bg-green-950/30';
    if (score >= 60) return 'text-amber-600 bg-amber-50 dark:bg-amber-950/30';
    return 'text-red-600 bg-red-50 dark:bg-red-950/30';
  };

  const details = resume ? resumeDetails(resume.content) : null;

  if (loading) {
    return (
      <section>
        <div className="mb-6">
          <button onClick={() => navigate('/resumes')} className="mb-4 flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100">
            <ArrowLeft size={20} />
            Back to Resumes
          </button>
          <h1 className="text-3xl font-bold">Loading...</h1>
        </div>
      </section>
    );
  }

  if (!resume) {
    return (
      <section>
        <div className="mb-6">
          <button onClick={() => navigate('/resumes')} className="mb-4 flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100">
            <ArrowLeft size={20} />
            Back to Resumes
          </button>
          <h1 className="text-3xl font-bold">{error || 'Resume not found'}</h1>
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex items-center justify-between">
        <button
          onClick={() => navigate('/resumes')}
          className="flex items-center gap-2 text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100"
        >
          <ArrowLeft size={20} />
          Back to Resumes
        </button>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setShowVersions(!showVersions)}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
            title="View version history"
          >
            <History size={18} />
          </button>
          <button
            onClick={handleSetPrimary}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
          >
            <Star size={18} className={resume.isPrimary ? 'fill-amber-400 text-amber-400' : ''} />
          </button>
          <button
            onClick={() => window.print()}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
            aria-label="Print or save resume as PDF"
            title="Print or save as PDF"
          >
            <Printer size={18} />
          </button>
          <button
            onClick={() => navigate(`/resumes/${id}/edit`)}
            className="rounded-lg border border-slate-300 p-2 text-slate-600 transition-colors hover:bg-slate-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
          >
            <Edit size={18} />
          </button>
          <button
            onClick={handleDelete}
            className="rounded-lg border border-red-300 p-2 text-red-600 transition-colors hover:bg-red-50 dark:border-red-900 dark:text-red-400 dark:hover:bg-red-950/30"
          >
            <Trash2 size={18} />
          </button>
        </div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="space-y-6"
      >
        {error && <p role="alert" className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}
        {/* Header */}
        <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <div className="flex items-center gap-3">
                <h1 className="text-2xl font-bold text-slate-950 dark:text-slate-50">{resume.title}</h1>
                {resume.isPrimary && <Star className="fill-amber-400 text-amber-400" size={20} />}
              </div>
              <p className="mt-2 text-sm text-slate-500">{resume.template} Template</p>
              
              <div className="mt-4 flex items-center gap-4">
                <div className={`flex items-center gap-1.5 rounded-full px-3 py-1 text-sm font-medium ${getAtsScoreColor(resume.atsScore || 0)}`}>
                  <Award size={16} />
                  ATS Score: {resume.atsScore || 0}/100
                </div>
                {resume.isPrimary && (
                  <span className="rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-700 dark:bg-blue-950/30 dark:text-blue-300">
                    Primary Resume
                  </span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Resume Content */}
        <div id="resume-document" className="rounded-xl border border-slate-200 bg-white p-8 shadow-soft print:border-0 print:shadow-none">
          <header className="border-b-2 border-slate-800 pb-4 text-center text-slate-900">
            <h2 className="text-2xl font-bold">{details.personal?.fullName || resume.title}</h2>
            {details.personal?.headline && <p className="mt-1 text-sm">{details.personal.headline}</p>}
            <p className="mt-2 text-xs text-slate-600">
              {[details.personal?.email, details.personal?.phone, details.personal?.location].filter(Boolean).join(' | ')}
            </p>
          </header>
          <ResumeSection title="Professional Summary" content={details.summary} />
          <ResumeSection title="Experience" content={details.experience} />
          <ResumeSection title="Education" content={details.education} />
          <ResumeSection title="Skills" content={details.skills} />
          <ResumeSection title="Projects" content={details.projects} />
          <ResumeSection title="Certifications" content={details.certifications} />
          {!resume.content && <p className="mt-5 text-sm text-slate-500">No resume sections added yet.</p>}
        </div>

        {/* Version History */}
        {showVersions && (
          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-soft dark:border-slate-800 dark:bg-slate-900">
            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold text-slate-950 dark:text-slate-50">
              <History size={20} />
              Version History ({versions.length})
            </h2>
            {versions.length === 0 ? (
              <p className="text-slate-500">No version history available.</p>
            ) : (
              <div className="space-y-3">
                {versions.map((version) => (
                  <div key={version.id} className="rounded-lg border border-slate-200 p-4 dark:border-slate-800">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium text-slate-950 dark:text-slate-50">Version {version.versionNumber}</p>
                        <p className="text-sm text-slate-500">{new Date(version.createdAt).toLocaleString()}</p>
                      </div>
                      {version.changeDescription && (
                        <p className="text-sm text-slate-600 dark:text-slate-400">{version.changeDescription}</p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </motion.div>
    </section>
  );
}
