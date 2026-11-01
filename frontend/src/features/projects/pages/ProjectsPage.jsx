import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Plus, Search, Filter, Star, Github, ExternalLink, Edit, Trash2, Eye } from 'lucide-react';
import { projectService } from '../services/projectService.js';
import { Link } from 'react-router-dom';

export function ProjectsPage() {
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadProjects();
  }, [page, statusFilter]);

  const loadProjects = async () => {
    try {
      setLoading(true);
      const params = { page, size: 9 };
      if (statusFilter !== 'ALL') {
        const data = await projectService.getByStatus(statusFilter, params);
        setProjects(data.content);
        setTotalPages(data.totalPages);
      } else if (searchQuery) {
        const data = await projectService.search(searchQuery, params);
        setProjects(data.content);
        setTotalPages(data.totalPages);
      } else {
        const data = await projectService.getAll(params);
        setProjects(data.content);
        setTotalPages(data.totalPages);
      }
    } catch (error) {
      console.error('Failed to load projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    loadProjects();
  };

  const handleDelete = async (id, title) => {
    if (!confirm(`Are you sure you want to delete "${title}"?`)) return;
    try {
      await projectService.delete(id);
      loadProjects();
    } catch (error) {
      console.error('Failed to delete project:', error);
    }
  };

  const handleToggleFeatured = async (id) => {
    try {
      await projectService.toggleFeatured(id);
      loadProjects();
    } catch (error) {
      console.error('Failed to toggle featured:', error);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-700 dark:bg-green-950 dark:text-green-300';
      case 'IN_PROGRESS': return 'bg-blue-100 text-blue-700 dark:bg-blue-950 dark:text-blue-300';
      case 'PLANNED': return 'bg-amber-100 text-amber-700 dark:bg-amber-950 dark:text-amber-300';
      case 'ON_HOLD': return 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300';
      default: return 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300';
    }
  };

  if (loading) {
    return (
      <section>
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-3xl font-bold">Projects</h1>
          <button className="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700">
            <Plus size={18} />
            Add Project
          </button>
        </div>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <div key={i} className="h-64 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />
          ))}
        </div>
      </section>
    );
  }

  return (
    <section>
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <h1 className="text-3xl font-bold">Projects</h1>
        <Link
          to="/projects/new"
          className="flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
        >
          <Plus size={18} />
          Add Project
        </Link>
      </div>

      {/* Search and Filter */}
      <div className="mb-6 flex flex-col gap-4 sm:flex-row">
        <form onSubmit={handleSearch} className="flex-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input
              type="text"
              placeholder="Search projects by title, description, or tech stack..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full rounded-lg border border-slate-300 py-2.5 pl-10 pr-4 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
            />
          </div>
        </form>
        <select
          value={statusFilter}
          onChange={(e) => {
            setStatusFilter(e.target.value);
            setPage(0);
          }}
          className="rounded-lg border border-slate-300 px-4 py-2.5 focus:border-blue-500 focus:outline-none dark:border-slate-700 dark:bg-slate-900"
        >
          <option value="ALL">All Status</option>
          <option value="COMPLETED">Completed</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="PLANNED">Planned</option>
          <option value="ON_HOLD">On Hold</option>
        </select>
      </div>

      {/* Projects Grid */}
      {projects.length === 0 ? (
        <div className="rounded-xl border border-slate-200 bg-white p-12 text-center shadow-soft dark:border-slate-800 dark:bg-slate-900">
          <div className="mx-auto mb-4 rounded-full bg-slate-100 p-4 dark:bg-slate-800">
            <Star className="text-slate-400" size={32} />
          </div>
          <h3 className="mb-2 text-lg font-semibold text-slate-950 dark:text-slate-50">No projects yet</h3>
          <p className="mb-4 text-slate-500">Start building your portfolio by adding your first project.</p>
          <Link
            to="/projects/new"
            className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-white transition-colors hover:bg-blue-700"
          >
            <Plus size={18} />
            Add Your First Project
          </Link>
        </div>
      ) : (
        <>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {projects.map((project, index) => (
              <motion.div
                key={project.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="group relative rounded-xl border border-slate-200 bg-white p-5 shadow-soft transition-all hover:shadow-md dark:border-slate-800 dark:bg-slate-900"
              >
                {project.featured && (
                  <div className="absolute right-3 top-3">
                    <Star className="fill-amber-400 text-amber-400" size={18} />
                  </div>
                )}
                
                <h3 className="mb-2 text-lg font-semibold text-slate-950 dark:text-slate-50 line-clamp-1">
                  {project.title}
                </h3>
                <p className="mb-4 text-sm text-slate-500 line-clamp-2">{project.description}</p>
                
                {project.techStack && (
                  <div className="mb-4 flex flex-wrap gap-1.5">
                    {project.techStack.split(',').slice(0, 3).map((tech, i) => (
                      <span
                        key={i}
                        className="rounded-full bg-slate-100 px-2 py-0.5 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-400"
                      >
                        {tech.trim()}
                      </span>
                    ))}
                  </div>
                )}
                
                <div className="mb-4 flex items-center gap-2">
                  <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${getStatusColor(project.status)}`}>
                    {project.status.replace('_', ' ')}
                  </span>
                  {project.screenshotCount > 0 && (
                    <span className="text-xs text-slate-500">{project.screenshotCount} screenshot{project.screenshotCount > 1 ? 's' : ''}</span>
                  )}
                </div>
                
                <div className="flex items-center gap-2">
                  {project.githubUrl && (
                    <a
                      href={project.githubUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                    >
                      <Github size={16} />
                    </a>
                  )}
                  {project.liveDemoUrl && (
                    <a
                      href={project.liveDemoUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                    >
                      <ExternalLink size={16} />
                    </a>
                  )}
                  <div className="flex-1" />
                  <Link
                    to={`/projects/${project.id}`}
                    className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                  >
                    <Eye size={16} />
                  </Link>
                  <Link
                    to={`/projects/${project.id}/edit`}
                    className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                  >
                    <Edit size={16} />
                  </Link>
                  <button
                    onClick={() => handleToggleFeatured(project.id)}
                    className="rounded-lg bg-slate-100 p-2 text-slate-600 transition-colors hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700"
                  >
                    <Star size={16} className={project.featured ? 'fill-amber-400 text-amber-400' : ''} />
                  </button>
                  <button
                    onClick={() => handleDelete(project.id, project.title)}
                    className="rounded-lg bg-red-50 p-2 text-red-600 transition-colors hover:bg-red-100 dark:bg-red-950/30 dark:text-red-400 dark:hover:bg-red-950/50"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              </motion.div>
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="mt-6 flex items-center justify-center gap-2">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="rounded-lg border border-slate-300 px-4 py-2 text-slate-600 transition-colors hover:bg-slate-50 disabled:opacity-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
              >
                Previous
              </button>
              <span className="text-sm text-slate-500">
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="rounded-lg border border-slate-300 px-4 py-2 text-slate-600 transition-colors hover:bg-slate-50 disabled:opacity-50 dark:border-slate-700 dark:text-slate-400 dark:hover:bg-slate-800"
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </section>
  );
}
