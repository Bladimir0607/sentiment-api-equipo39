import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  BrainCircuit, BarChart3, MessageSquare, Languages, 
  Zap, Code2, LogOut, Loader2, Menu, X, Sparkles, Activity, ShieldCheck
} from 'lucide-react';
import { Toaster, toast } from 'sonner';

// Servicios y Componentes
import { analyzeText } from './api/sentimentApi';
import { useAuth } from './context/AuthContext';
import { useI18n } from './context/LanguageContext'; 
import Login from './pages/Login';
import Stats from './pages/Stats';
import TechInfoModal from './components/TechInfoModal';
import StarBackground from './components/StarBackground';

function App() {
  const [view, setView] = useState('analyzer'); 
  const { user, logout } = useAuth();
  const { t, lang, changeLanguage, loading: i18nLoading } = useI18n();
  
  const [text, setText] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const getKeywords = (inputText) => {
    return inputText
      .split(/\s+/)
      .filter(word => word.length > 4)
      .slice(0, 5);
  };

  if (!user) return <><StarBackground /><Login /></>;

  if (i18nLoading) {
    return (
      <div className="min-h-screen bg-[#0a0510] flex items-center justify-center">
        <Loader2 className="text-purple-500 animate-spin" size={48} />
      </div>
    );
  }

  const onAnalyze = async () => {
    if (text.length < 5) {
      toast.error(t('error.text.length') || "Texto muy corto");
      return;
    }
    setLoading(true);
    setResult(null); 
    try {
      const data = await analyzeText(text, lang);
      setTimeout(() => {
        setResult(data);
        setLoading(false);
        toast.success(t('sentiment.analyze.success'));
      }, 800);
    } catch (err) {
      setLoading(false);
      toast.error(err?.response?.data?.message || t('sentiment.analyze.error'));
    }
  };

  return (
    <div className="min-h-screen bg-[#0a0510] text-white overflow-x-hidden relative font-sans selection:bg-purple-500/30">
      <StarBackground />
      <Toaster position="bottom-right" richColors theme="dark" />

      {/* Navbar */}
      <nav className="fixed top-0 w-full z-50 backdrop-blur-xl border-b border-white/5 px-4">
        <div className="max-w-6xl mx-auto py-4 flex justify-between items-center">
          <motion.div 
            whileHover={{ scale: 1.05 }}
            className="flex items-center gap-2 cursor-pointer z-50" 
            onClick={() => setView('analyzer')}
          >
            <div className="bg-purple-600 p-2 rounded-lg shadow-[0_0_15px_rgba(168,85,247,0.4)]">
              <BrainCircuit size={20} className="text-white" />
            </div>
            <span className="text-xl md:text-2xl font-black tracking-tighter bg-linear-to-r from-white to-gray-500 bg-clip-text text-transparent">
              SENTIM<span className="text-purple-500">AI</span>
            </span>
          </motion.div>

          <button className="md:hidden z-50 p-2 text-gray-400" onClick={() => setIsMenuOpen(!isMenuOpen)}>
            {isMenuOpen ? <X size={28} /> : <Menu size={28} />}
          </button>

          <div className={`
            fixed md:relative top-0 left-0 w-full md:w-auto h-screen md:h-auto 
            bg-[#0a0510]/98 md:bg-transparent backdrop-blur-2xl md:backdrop-blur-none
            flex flex-col md:flex-row items-center justify-center md:justify-end gap-8 md:gap-6
            transition-all duration-300 z-40
            ${isMenuOpen ? 'translate-x-0' : 'translate-x-full md:translate-x-0'}
          `}>
            {user.role === 'ADMIN' && (
              <button
                onClick={() => { setView('stats'); setIsMenuOpen(false); }}
                className={`flex items-center gap-2 font-black text-sm md:text-xs tracking-widest transition-colors ${
                  view === 'stats' ? 'text-purple-400' : 'text-gray-400 hover:text-white'
                }`}
              >
                <BarChart3 size={18} /> {t('nav.stats')}
              </button>
            )}

            <div className="flex items-center gap-2 bg-white/5 px-4 py-2 md:px-3 md:py-1.5 rounded-xl border border-white/10 hover:border-purple-500/30 transition-all">
              <Languages size={16} className="text-purple-400" />
              <select
                value={lang}
                onChange={(e) => changeLanguage(e.target.value)}
                className="bg-transparent text-sm md:text-xs outline-none font-bold cursor-pointer appearance-none uppercase tracking-widest pr-2"
              >
                <option value="es" className="bg-[#130b1d]">ES</option>
                <option value="en" className="bg-[#130b1d]">EN</option>
                <option value="pt" className="bg-[#130b1d]">PT</option>
              </select>
            </div>

            <div className="flex flex-col md:flex-row items-center gap-4 border-l border-white/10 pl-6">
              <div className="text-center md:text-right">
                <p className="text-[10px] font-black text-purple-500 tracking-tighter uppercase">{user.role}</p>
                <p className="text-base md:text-sm font-bold text-gray-200">{user.username}</p>
              </div>
              <button onClick={logout} className="p-2 bg-white/5 border border-white/10 rounded-xl text-gray-400 hover:text-red-400 hover:bg-red-500/10 transition-all">
                <LogOut size={18} />
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="relative z-10 pt-28 md:pt-36 pb-20 px-4 md:px-6 max-w-6xl mx-auto">
        <AnimatePresence mode="wait">
          {view === 'analyzer' ? (
            <motion.div key="analyzer" initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }} className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
              
              <div className="lg:col-span-8 space-y-8">
                <div className="relative group">
                  <div className="absolute -inset-1 bg-linear-to-r from-purple-600 to-blue-600 rounded-3xl blur opacity-10 group-focus-within:opacity-30 transition duration-500" />
                  <div className="relative bg-[#130b1d]/80 backdrop-blur-md border border-white/10 rounded-3xl p-6 md:p-10 shadow-2xl overflow-hidden">
                    
                    {loading && (
                      <motion.div 
                        initial={{ top: '-10%' }}
                        animate={{ top: '110%' }}
                        transition={{ duration: 1.5, repeat: Infinity, ease: "linear" }}
                        className="absolute left-0 w-full h-20 bg-linear-to-b from-transparent via-purple-500/10 to-transparent z-10 pointer-events-none"
                      />
                    )}

                    <h2 className="text-lg md:text-xl font-black mb-8 flex items-center gap-3 tracking-tight uppercase">
                      <MessageSquare className={loading ? "text-purple-500 animate-pulse" : "text-purple-500"} /> 
                      {loading ? "PROCESANDO_DATOS..." : t('title.analysis')}
                    </h2>

                    <textarea
                      value={text}
                      onChange={(e) => setText(e.target.value)}
                      placeholder={t('placeholder.text')}
                      className="w-full h-44 bg-transparent text-base md:text-lg outline-none resize-none text-gray-200 placeholder:text-gray-700 font-medium leading-relaxed z-20 relative"
                    />

                    <div className="mt-6">
                      <p className="text-[9px] font-black text-gray-600 tracking-widest mb-3 uppercase flex items-center gap-2">
                        <Activity size={10} /> {t('analyzer.suggestions') || 'SUGERENCIAS'}
                      </p>
                      <div className="flex flex-wrap gap-2">
                        {[
                          t('prompt.happy') || 'Excelente interfaz y muy rapido',
                          t('prompt.sad') || 'O sistema está muito lento hoje',
                          t('prompt.neutral') || 'The analysis is complete'
                        ].map((suggestion, i) => (
                          <button
                            key={i}
                            onClick={() => setText(suggestion)}
                            className="text-[9px] font-bold px-3 py-1.5 rounded-lg border border-white/5 bg-white/5 text-gray-500 hover:text-purple-400 hover:bg-purple-500/5 transition-all uppercase"
                          >
                            {suggestion.length > 25 ? suggestion.substring(0, 25) + "..." : suggestion}
                          </button>
                        ))}
                      </div>
                    </div>

                    {/* SECCIÓN DE SEGURIDAD Y ACTIVIDAD */}
                    <div className="mt-8 flex items-center justify-between px-1">
                      <div className="flex items-center gap-4">
                        <div className="flex -space-x-2">
                          {[1, 2, 3].map((i) => (
                            <div key={i} className="w-5 h-5 rounded-full border-2 border-[#130b1d] bg-purple-900 flex items-center justify-center">
                              <div className="w-1 h-1 bg-purple-400 rounded-full animate-pulse" />
                            </div>
                          ))}
                        </div>
                        <p className="text-[9px] text-gray-500 font-medium tracking-tight">
                          <span className="text-purple-400 font-bold">+</span> {t('analyzer.active_users') || 'análisis realizados hoy'}
                        </p>
                      </div>
                      
                      <div className="flex items-center gap-2 text-[9px] font-black text-gray-600 uppercase tracking-[0.2em]">
                        <ShieldCheck size={12} className="text-emerald-500/50" />
                        End-to-End Encrypted
                      </div>
                    </div>

                    <div className="flex flex-col sm:flex-row justify-between items-center mt-8 pt-8 border-t border-white/5 gap-6">
                      <div className="flex items-center gap-3">
                        <div className={`w-2 h-2 rounded-full ${loading ? 'bg-purple-500 animate-ping' : 'bg-emerald-500'}`} />
                        <span className="text-[10px] text-gray-600 font-black tracking-[0.3em] uppercase">
                          {loading ? 'STATUS: ANALYZING' : 'STATUS: READY'}
                        </span>
                      </div>
                      <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={onAnalyze}
                        disabled={loading || text.length < 5}
                        className="w-full sm:w-auto bg-linear-to-r from-purple-600 to-blue-600 px-10 py-4 rounded-2xl font-black text-xs tracking-[0.2em] transition-all disabled:opacity-30 shadow-xl shadow-purple-500/20 flex justify-center items-center gap-2 group overflow-hidden relative"
                      >
                        {loading ? (
                          <Loader2 className="animate-spin" size={16} />
                        ) : (
                          <span className="relative z-10 flex items-center gap-2">
                            {t('button.analyze')} <Zap size={16} className="group-hover:fill-yellow-400 transition-colors" />
                          </span>
                        )}
                      </motion.button>
                    </div>
                  </div>
                </div>

                {/* Card de Resultado Refinada con Keywords Dinámicas */}
                <AnimatePresence>
                  {result && (result.prediction || result.prevision) && (
                    <motion.div 
                      initial={{ opacity: 0, y: 40, filter: 'blur(10px)' }} 
                      animate={{ opacity: 1, y: 0, filter: 'blur(0px)' }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      transition={{ type: "spring", damping: 20 }}
                      className={`p-8 md:p-10 rounded-[2.5rem] border-2 shadow-2xl overflow-hidden relative ${
                        (result.prediction || result.prevision)?.toLowerCase() === 'positivo' 
                         ? 'border-emerald-500/30 bg-emerald-500/5 text-emerald-400' 
                         : 'border-red-500/30 bg-red-500/5 text-red-400'
                      }`}
                    >
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-10 items-center relative z-10">
                        <div>
                          <p className="text-[10px] font-black uppercase tracking-[0.4em] mb-3 opacity-60">
                            SENTIMENT_ANALYSIS_RESULT
                          </p>
                          <motion.h3 
                            initial={{ x: -20 }} animate={{ x: 0 }}
                            className="text-5xl md:text-7xl font-black italic tracking-tighter uppercase mb-8"
                          >
                            {(result.prediction || result.prevision)}
                          </motion.h3>
                          
                          <div className="space-y-4">
                            <p className="text-[10px] font-black tracking-[0.2em] opacity-40 flex items-center gap-2 uppercase">
                              <Code2 size={12} /> {t('result.keywords') || 'Detected Tokens'}
                            </p>
                            <div className="flex flex-wrap gap-2">
                              {getKeywords(text).map((word, i) => {
                                // Color dinámico según el resultado
                                const isPos = (result.prediction || result.prevision)?.toLowerCase() === 'positivo';
                                return (
                                  <motion.span 
                                    initial={{ opacity: 0, scale: 0.8 }}
                                    animate={{ opacity: 1, scale: 1 }}
                                    transition={{ delay: i * 0.1 }}
                                    key={i} 
                                    className={`px-3 py-1.5 rounded-lg text-[10px] font-mono font-bold border transition-colors ${
                                      isPos 
                                      ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400' 
                                      : 'bg-red-500/10 border-red-500/30 text-red-400'
                                    }`}
                                  >
                                    <span className="opacity-50 mr-1">#</span>{word}
                                  </motion.span>
                                );
                              })}
                            </div>
                          </div>
                        </div>

                        <div className="flex flex-col items-center md:items-end md:border-l border-white/10 md:pl-10">
                          <div className="text-center md:text-right mb-4">
                              <span className="text-[9px] font-black px-4 py-1.5 rounded-full border border-current mb-4 inline-block tracking-widest uppercase">
                                {(result.probability || result.probabilidad) > 0.8 ? 'High Reliability' : 'Medium Reliability'}
                              </span>
                              <p className="text-[10px] font-black opacity-50 tracking-widest uppercase">{t('result.confidence')}</p>
                          </div>
                          <p className="text-6xl md:text-8xl font-black font-mono tracking-tighter">
                            {(Number(result.probability || result.probabilidad || 0) * 100).toFixed(1)}%
                          </p>
                        </div>
                      </div>
                      <div className="absolute top-0 right-0 w-64 h-64 bg-current opacity-[0.03] rounded-full blur-3xl -mr-32 -mt-32" />
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>

              {/* Columna Derecha */}
              <div className="lg:col-span-4 space-y-6">
                <motion.div 
                  whileHover={{ y: -5 }}
                  className="bg-white/5 border border-white/10 rounded-4xl p-8 backdrop-blur-sm relative overflow-hidden group transition-all"
                >
                  <div className="flex justify-between items-start mb-6">
                    <h3 className="font-black text-[10px] tracking-[0.3em] text-purple-500 uppercase">{t('title.system.status') || 'System Health'}</h3>
                    <div className="flex h-3 w-3 relative">
                      <span className="animate-ping absolute h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                      <span className="h-full w-full rounded-full bg-emerald-500 shadow-[0_0_10px_rgba(16,185,129,0.5)]"></span>
                    </div>
                  </div>
                  <div className="space-y-4 font-mono">
                    <div className="flex justify-between items-center text-xs">
                      <span className="text-gray-500">ENGINE</span>
                      <span className="text-emerald-400 font-bold tracking-widest">ONLINE</span>
                    </div>
                    <div className="flex justify-between items-center text-xs">
                      <span className="text-gray-500">LATENCY</span>
                      <span className="text-purple-400">24ms</span>
                    </div>
                    <div className="w-full bg-white/5 h-px my-2" />
                    <div className="text-[10px] text-gray-600 flex items-center gap-2">
                       <Sparkles size={10} /> ENCRYPTED_CONNECTION_ACTIVE
                    </div>
                  </div>
                </motion.div>

                <motion.div 
                  whileHover={{ y: -5 }}
                  className="relative group cursor-pointer"
                  onClick={() => setIsModalOpen(true)}
                >
                  <div className="absolute -inset-px bg-linear-to-br from-purple-600/30 to-blue-600/30 rounded-4xl blur-[2px] group-hover:opacity-100 transition duration-500 opacity-30" />
                  <div className="relative bg-[#0f0a18] p-8 rounded-4xl border border-white/5 overflow-hidden shadow-2xl">
                    <div className="absolute top-0 -left-full w-full h-full bg-linear-to-r from-transparent via-white/3 to-transparent skew-x-[-25deg] group-hover:animate-[shimmer_2s_infinite]" />
                    
                    <div className="flex justify-between items-center mb-6">
                      <div className="bg-linear-to-br from-purple-600 to-blue-600 w-12 h-12 rounded-2xl flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform">
                        <Zap className="text-white" size={20} fill="currentColor" />
                      </div>
                      <div className="text-[8px] font-black text-purple-500/50 tracking-widest uppercase">LABS_ACCESS</div>
                    </div>

                    <h3 className="font-black text-white italic tracking-tighter text-2xl mb-4">
                      SentimAI <span className="text-purple-500">Labs</span>
                    </h3>
                    <p className="text-[11px] text-gray-500 leading-relaxed font-medium border-l border-purple-500/30 pl-4 italic">
                      {t('card.tip.desc') || 'Utilizando Transformers de última generación para decodificación emocional.'}
                    </p>
                  </div>
                </motion.div>
              </div>
            </motion.div>
          ) : (
            <Stats onBack={() => setView('analyzer')} />
          )}
        </AnimatePresence>
      </main>

      {/* Floating Info Button */}
      <motion.button 
        whileHover={{ scale: 1.1, rotate: 5 }}
        whileTap={{ scale: 0.9 }}
        onClick={() => setIsModalOpen(true)} 
        className="fixed bottom-8 right-8 md:left-8 md:right-auto p-5 bg-[#130b1d] border border-white/10 rounded-2xl hover:border-purple-500 transition-all group z-40 shadow-2xl hover:shadow-purple-500/40"
      >
        <Code2 className="text-purple-400 group-hover:text-white transition-all" size={24} />
      </motion.button>

      <TechInfoModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} />

      <style jsx>{`
        @keyframes shimmer {
          100% { left: 200%; }
        }
      `}</style>
    </div>
  );
}

export default App;