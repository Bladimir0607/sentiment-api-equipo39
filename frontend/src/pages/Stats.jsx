import React, { useEffect, useState, useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { BarChart, Bar, XAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { getStats, getHistory } from '../api/sentimentApi';
import { ArrowLeft, TrendingUp, Globe, Activity, MessageSquare, ShieldCheck, Search } from 'lucide-react';
import { useI18n } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext'; // <--- Importante

const Stats = ({ onBack }) => {
  const { t } = useI18n();
  const { user } = useAuth(); // Obtenemos el rol
  const isAdmin = user?.role === 'ADMIN';

  const [data, setData] = useState({ total: 0, positive: 0, neutral: 0, negative: 0 });
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // 1. Siempre traemos el historial del usuario logueado
        const historyRes = await getHistory().catch(() => []);
        const historyData = Array.isArray(historyRes) ? historyRes : (historyRes?.content || []);
        setHistory(historyData);

        if (isAdmin) {
          // 2. Si es ADMIN, traemos las estadísticas globales del servidor
          const statsRes = await getStats().catch(() => ({ total: 0, positive: 0, neutral: 0, negative: 0 }));
          setData(statsRes);
        } else {
          // 3. Si es USER, calculamos sus propias estadísticas basándonos en su historial personal
          const personalStats = historyData.reduce((acc, curr) => {
            const rawSentiment = (curr.prediction || curr.label || curr.sentiment || '').toUpperCase();
            acc.total++;
            if (rawSentiment.includes('POS')) acc.positive++;
            else if (rawSentiment.includes('NEG')) acc.negative++;
            else acc.neutral++;
            return acc;
          }, { total: 0, positive: 0, neutral: 0, negative: 0 });
          setData(personalStats);
        }
      } catch (error) {
        console.error("Error crítico en Stats:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [isAdmin]);

  const filteredHistory = useMemo(() => {
    return history.filter(item => {
      const text = (item.originalText || item.text || item.content || "").toLowerCase();
      const pred = (item.prediction || item.label || item.sentiment || "").toLowerCase();
      return text.includes(searchTerm.toLowerCase()) || pred.includes(searchTerm.toLowerCase());
    });
  }, [history, searchTerm]);

  const total = data?.total || 0;
  const chartData = [
    { name: t('stats.pos') || 'Positives', value: data?.positive || 0, color: '#10b981' },
    { name: t('stats.neu') || 'Neutrals', value: data?.neutral || 0, color: '#f59e0b' },
    { name: t('stats.neg') || 'Negatives', value: data?.negative || 0, color: '#ef4444' },
  ].map(item => ({
    ...item,
    percentage: total > 0 ? ((item.value / total) * 100).toFixed(1) : "0"
  }));

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-96 text-purple-400">
        <motion.div animate={{ rotate: 360 }} transition={{ duration: 2, repeat: Infinity, ease: "linear"}} className='mb-4'>
          <Activity size={48} />
        </motion.div>
        <p className="font-mono text-sm uppercase tracking-[0.3em] animate-pulse">Ml Syncing Roles...</p>
      </div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="p-4 md:p-8 max-w-6xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <button onClick={onBack} className="flex items-center gap-2 text-gray-400 hover:text-purple-400 transition-all group font-bold tracking-widest text-[10px] md:text-xs uppercase">
        <ArrowLeft size={16} className="group-hover:-translate-x-2 transition-transform" /> 
        {t('stats.back')}
      </button>
        <div className="text-right">
            <div className={`text-[10px] font-black uppercase tracking-widest flex items-center gap-2 justify-end ${isAdmin ? 'text-red-500' : 'text-purple-500'}`}>
              <span className={`w-1.5 h-1.5 rounded-full animate-ping ${isAdmin ? 'bg-red-500' : 'bg-purple-500'}`} />
              {isAdmin ? '🔴 ADMIN GLOBAL VIEW' : `🔵 USER DASHBOARD: ${user?.username}`}
            </div>
            <div className="text-[9px] text-gray-500 font-mono mt-1">
                {isAdmin ? 'Monitoring all system neural nodes' : 'Your private analysis workspace'}
            </div>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 md:gap-6 mb-8">
        <StatCard title={isAdmin ? "GLOBAL ANALYSES" : "YOUR ANALYSES"} value={total} icon={<TrendingUp size={20} />} color={isAdmin ? "red" : "purple"} />
        <StatCard title={t('stats.precision')} value="94.2%" icon={<ShieldCheck size={20} />} color="green" />
        <StatCard title={t('stats.uptime')} value="99.9%" icon={<Globe size={20} />} color="blue" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-7 bg-[#0f0817] p-6 md:p-8 rounded-[2.5rem] border border-white/5 shadow-2xl relative overflow-hidden">
          <h3 className="text-xl font-black flex items-center gap-3 mb-8 uppercase tracking-tighter relative z-10">
            <span className={`w-2 h-2 rounded-full animate-pulse ${isAdmin ? 'bg-red-500' : 'bg-purple-500'}`} />
            {isAdmin ? 'System Sentiment Distribution' : 'My Sentiment History'}
          </h3>
          <div className="h-80 w-full relative z-10">
          <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData}>
                <XAxis dataKey="name" axisLine={false} tickLine={false} tick={{ fill: '#64748b', fontSize: 10, fontWeight: 'bold' }} dy={10} />
              <Tooltip 
                cursor={{ fill: 'white', opacity: 0.05 }}
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    return (
                        <div className="bg-[#1a1025] border border-purple-500/30 p-4 rounded-2xl shadow-2xl">
                          <p className="text-[10px] text-purple-400 uppercase font-bold mb-1">{payload[0].payload.name}</p>
                          <p className="text-2xl font-black text-white">{payload[0].value}</p>
                          <p className="text-[9px] text-gray-500">{payload[0].payload.percentage}% of total</p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
                <Bar dataKey="value" radius={[10, 10, 0, 0]} barSize={50}>
                  {chartData.map((entry, index) => <Cell key={index} fill={entry.color} fillOpacity={0.8} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

        <div className="lg:col-span-5 bg-[#0f0817] p-6 md:p-8 rounded-[2.5rem] border border-white/5 shadow-2xl flex flex-col">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-xl font-black flex items-center gap-3 uppercase tracking-tighter">
              <MessageSquare className="text-blue-500" size={20} /> Logs
            </h3>
            <div className="relative group">
                <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
                <input 
                    type="text" 
                    placeholder="Search..." 
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="bg-white/5 border border-white/10 rounded-full py-1.5 pl-9 pr-4 text-[10px] text-white focus:outline-none focus:border-purple-500/50 w-28 focus:w-40 transition-all"
                />
            </div>
          </div>

          <div className="space-y-3 overflow-y-auto max-h-[420px] pr-2 custom-scrollbar">
            <AnimatePresence mode='popLayout'>
                {filteredHistory.length > 0 ? filteredHistory.map((item, idx) => {
                    const rawSentiment = (item.prediction || item.label || item.sentiment || 'NEUTRAL').toUpperCase();
                    const isPos = rawSentiment.includes('POS');
                    const isNeg = rawSentiment.includes('NEG');
                    const label = isPos ? 'POSITIVE' : isNeg ? 'NEGATIVE' : 'NEUTRAL';

                    return (
                        <motion.div layout initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} key={item.id || idx} 
                            className="p-4 bg-white/5 border border-white/5 rounded-2xl hover:bg-white/10 transition-all group">
                            <div className="flex justify-between items-center mb-2">
                                <span className={`text-[9px] font-black px-2 py-0.5 rounded-md border uppercase tracking-widest ${
                                    isPos ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20' :
                                    isNeg ? 'bg-red-500/10 text-red-400 border-red-500/20' :
                                    'bg-amber-500/10 text-amber-400 border-amber-500/20'
                                }`}>
                                    {label}
                                </span>
                                <span className="text-[10px] font-mono text-gray-500 font-bold">
                                    {item.probability ? `${(item.probability * 100).toFixed(0)}%` : '--'}
                                </span>
                            </div>
                            <p className="text-xs text-gray-300 line-clamp-2 italic">
                                "{item.originalText || item.text || item.content || '...'}"
                            </p>
                        </motion.div>
                    );
                }) : (
                    <div className="flex flex-col items-center justify-center py-20 opacity-20">
                        <MessageSquare size={40} />
                        <p className="text-[10px] font-black uppercase mt-4 tracking-widest">No history yet</p>
                    </div>
                )}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </motion.div>
  );
};

// He actualizado el StatCard para que soporte el color rojo de Admin
const StatCard = ({ title, value, icon, color }) => {
  const colors = {
    purple: "border-purple-500/20 text-purple-400 shadow-purple-500/5",
    red: "border-red-500/20 text-red-400 shadow-red-500/5",
    green: "border-emerald-500/20 text-emerald-400 shadow-emerald-500/5",
    blue: "border-blue-500/20 text-blue-400 shadow-blue-500/5"
  };
  return (
    <div className={`p-6 rounded-3xl bg-white/5 border ${colors[color]} hover:bg-white/[0.08] transition-all group`}>
      <div className="flex justify-between items-start mb-4">
        <span className="text-gray-500 text-[10px] uppercase font-black tracking-[0.2em]">{title}</span>
        <div className="opacity-50 group-hover:opacity-100 transition-opacity">{icon}</div>
      </div>
      <div className="text-3xl font-black text-white tracking-tighter">{value}</div>
    </div>
  );
};

export default Stats;