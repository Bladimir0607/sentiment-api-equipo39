import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Box, Code2, Database, Cpu, X, Users, Link2, 
  FileCode2, ShieldCheck, Microscope, ExternalLink, Sparkles 
} from 'lucide-react';
import { useI18n } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext'; 

const TechInfoModal = ({ isOpen, onClose }) => {
  const { t } = useI18n();
  const { user } = useAuth(); 

  const stack = [
    { icon: <Code2 size={18}/>, name: 'Backend', tech: 'Java 21 / Spring Boot 4.x' },
    { icon: <Cpu size={18}/>, name: 'Machine Learning', tech: 'Python / Scikit-Learn' },
    { icon: <Database size={18}/>, name: 'Persistencia', tech: 'MySQL / Flyway' },
    { icon: <Box size={18}/>, name: 'DevOps', tech: 'Docker / Testcontainers' },
  ];

  const team = [
    { name: 'Jhonatan Osorio', link: 'https://www.linkedin.com/in/jhonatan-o25/' },
    { name: 'Mario Perez', link: 'https://www.linkedin.com/in/mario-hamming/' },
    { name: 'Bladimir Ventura', link: 'https://www.linkedin.com/in/bladimir-v-15727b219' },
    { name: 'Yohan Ospina', link: 'https://www.linkedin.com/in/yohan-sebastian-ospina-gonz%C3%A1lez-92864729b/' },
    { name: 'Víctor Bardales', link: 'https://www.linkedin.com/in/hugobardales/' },
    { name: 'Julio Serrepe', link: 'https://www.linkedin.com/in/julio-alejandro-serrepe-ramirez/' },
  ];

  const docs = [
    { name: 'Swagger UI', desc: t('modal.tech.swagger_desc'), url: 'http://140.84.161.47:8080/swagger-ui.html', icon: <Code2 size={18} className="text-purple-400" /> },
    { name: 'JavaDoc', desc: t('modal.tech.javadoc_desc'), url: 'http://140.84.161.47:8080/docs/apidocs/index.html', icon: <FileCode2 size={18} className="text-blue-400" /> },
    { name: 'JaCoCo Report', desc: t('modal.tech.jacoco_desc'), url: 'http://140.84.161.47:8080/docs/jacoco/index.html', icon: <ShieldCheck size={18} className="text-emerald-400" /> }
  ];
  
  const isAdmin = user?.role === 'ADMIN';

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-60 flex items-center justify-center p-4 overflow-hidden">
          {/* Overlay con Blur dinámico */}
          <motion.div 
            initial={{ opacity: 0 }} 
            animate={{ opacity: 1 }} 
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-black/60 backdrop-blur-xl" 
          />

          <motion.div 
            initial={{ opacity: 0, scale: 0.9, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.9, y: 20 }}
            transition={{ type: "spring", damping: 25, stiffness: 300 }}
            className="relative bg-[#0f0a18]/95 border border-purple-500/30 w-full max-w-2xl rounded-4xl overflow-hidden shadow-[0_0_50px_rgba(168,85,247,0.15)] z-10 flex flex-col max-h-[85vh]"
          >
            {/* Header */}
            <div className="p-6 border-b border-white/5 flex justify-between items-center bg-linear-to-r from-purple-600/10 to-transparent relative">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-purple-600 rounded-2xl shadow-[0_0_15px_rgba(168,85,247,0.4)]">
                  <Microscope size={22} className="text-white" />
                </div>
                <div>
                  <h2 className="text-xl font-black text-white tracking-widest uppercase italic flex items-center gap-2">
                    {t('modal.tech.title')} <Sparkles size={14} className="text-purple-400 animate-pulse" />
                  </h2>
                  <p className="text-[10px] text-purple-400 font-black tracking-[0.3em] uppercase opacity-70">{t('modal.tech.subtitle')}</p>
                </div>
              </div>
              <button 
                onClick={onClose} 
                className="group p-2 bg-white/5 hover:bg-red-500/20 rounded-xl transition-all border border-white/5 hover:border-red-500/50"
              >
                <X size={20} className="text-gray-400 group-hover:text-red-400 transition-colors" />
              </button>
            </div>

            {/* Body */}
            <div className="p-6 md:p-8 space-y-8 overflow-y-auto custom-scrollbar relative">
              
              {/* Grid de Tecnologías */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {stack.map((item, idx) => (
                  <motion.div 
                    whileHover={{ scale: 1.02, backgroundColor: "rgba(255,255,255,0.03)" }}
                    key={idx} 
                    className="bg-white/5 p-4 rounded-2xl border border-white/10 flex items-center gap-4 transition-all"
                  >
                    <div className="p-3 bg-linear-to-br from-purple-500/20 to-blue-500/20 rounded-xl text-purple-400 border border-purple-500/10">
                      {item.icon}
                    </div>
                    <div className="min-w-0">
                      <h3 className="text-[10px] font-black text-purple-500/70 uppercase tracking-tighter">{item.name}</h3>
                      <p className="text-sm text-gray-200 font-bold truncate">{item.tech}</p>
                    </div>
                  </motion.div>
                ))}
              </div>

              {/* Documentación (Solo Admin) */}
              {isAdmin && (
                <div className="space-y-4">
                  <h3 className="font-black text-[11px] text-white/40 flex items-center gap-2 uppercase tracking-[0.4em]">
                    <ShieldCheck size={14} className="text-emerald-500" /> SYSTEM_DOCS
                  </h3>
                  <div className="grid grid-cols-1 gap-3">
                    {docs.map((doc, idx) => (
                      <a 
                        key={idx} 
                        href={doc.url} 
                        target="_blank" 
                        rel="noreferrer" 
                        className="flex items-center justify-between p-4 bg-white/5 border border-white/5 rounded-2xl hover:border-purple-500/40 hover:bg-purple-500/5 transition-all group"
                      >
                        <div className="flex items-center gap-4 overflow-hidden">
                          <div className="p-2 bg-black/40 rounded-xl group-hover:rotate-12 transition-transform">
                            {doc.icon}
                          </div>
                          <div className="overflow-hidden">
                            <h4 className="text-sm font-bold text-white uppercase tracking-tight">{doc.name}</h4>
                            <p className="text-[10px] text-gray-500 font-medium truncate italic">{doc.desc}</p>
                          </div>
                        </div>
                        <ExternalLink size={16} className="text-gray-600 group-hover:text-white group-hover:translate-x-1 transition-all" />
                      </a>
                    ))}
                  </div>
                </div>
              )}

              {/* Team Section */}
              <div className="pt-6 border-t border-white/5">
                <h3 className="font-black text-[11px] text-white/40 flex items-center gap-2 uppercase tracking-[0.4em] mb-4">
                  <Users size={14} /> CONTRIBUTORS
                </h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                  {team.map((member, idx) => (
                    <a 
                      key={idx} 
                      href={member.link} 
                      target="_blank" 
                      rel="noreferrer" 
                      className="flex items-center justify-between p-3 bg-white/5 border border-white/5 rounded-xl hover:border-purple-500/40 hover:bg-white/10 transition-all group"
                    >
                      <span className="text-[10px] font-bold text-gray-400 group-hover:text-purple-300 truncate">
                        {member.name.split(' ')[0]} {member.name.split(' ')[1]?.charAt(0)}.
                      </span>
                      <Link2 size={12} className="text-gray-700 group-hover:text-purple-400 transition-colors" />
                    </a>
                  ))}
                </div>
              </div>
            </div>
          </motion.div>
          
          <style jsx>{`
            .custom-scrollbar::-webkit-scrollbar { width: 4px; }
            .custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
            .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(168, 85, 247, 0.2); border-radius: 10px; }
            .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(168, 85, 247, 0.5); }
          `}</style>
        </div>
      )}
    </AnimatePresence>
  );
};

export default TechInfoModal;