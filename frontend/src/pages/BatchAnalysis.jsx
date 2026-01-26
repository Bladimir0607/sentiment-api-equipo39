import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next'; // Importamos el hook
import { analyzeBatch } from '../api/sentimentApi';
import { 
  Loader2, AlertCircle, ArrowLeft, 
  FileSpreadsheet, Activity, Zap 
} from 'lucide-react';

const BatchAnalysis = ({ onBack }) => { 
    const { t } = useTranslation(); // Inicializamos la traducción
    const [file, setFile] = useState(null);
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
        setError(null);
    };

    const handleUpload = async () => {
        if (!file) {
            setError(t("batch.error_file"));
            return;
        }

        setLoading(true);
        setError(null);
        try {
            const data = await analyzeBatch(file);
            setResults(data);
        } catch (err) {
            setError(t("batch.error_process"));
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const stats = {
        total: results.length,
        pos: results.filter(r => r.prediction?.toLowerCase().includes('pos') || r.prediction?.toLowerCase().includes('good')).length,
        neg: results.filter(r => r.prediction?.toLowerCase().includes('neg') || r.prediction?.toLowerCase().includes('bad')).length,
        neu: results.filter(r => r.prediction?.toLowerCase().includes('neu')).length,
    };

    return (
        <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-8"
        >
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <button 
                        onClick={onBack}
                        className="flex items-center gap-2 text-[10px] font-black text-purple-500 uppercase tracking-[0.3em] mb-2 hover:text-white transition-colors"
                    >
                        <ArrowLeft size={14} /> {t("batch.back")}
                    </button>
                    <h1 className="text-3xl md:text-4xl font-black italic tracking-tighter uppercase">
                        Batch <span className="text-purple-500 font-black">{t("batch.title")}</span>
                    </h1>
                </div>
                
                <div className="flex items-center gap-2 bg-white/5 px-4 py-2 rounded-xl border border-white/10">
                    <div className="w-2 h-2 rounded-full bg-blue-500 animate-pulse" />
                    <span className="text-[10px] font-black tracking-widest text-gray-400">CSV_ENGINE_READY</span>
                </div>
            </div>

            <div className="relative group">
                <div className="absolute -inset-1 bg-linear-to-r from-blue-600 to-purple-600 rounded-3xl blur opacity-10 group-focus-within:opacity-20 transition duration-500" />
                <div className="relative bg-[#130b1d]/80 backdrop-blur-md border border-white/10 rounded-3xl p-8 shadow-2xl overflow-hidden">
                    <div className="flex flex-col items-center text-center py-6">
                        <div className="bg-blue-500/10 p-5 rounded-2xl mb-4 border border-blue-500/20">
                            <FileSpreadsheet className="h-10 w-10 text-blue-400" />
                        </div>
                        <p className="text-gray-300 font-bold mb-1">{t("batch.upload_title")}</p>
                        <p className="text-xs text-gray-500 uppercase tracking-widest mb-8">{t("batch.upload_formats")}</p>
                        
                        <div className="w-full max-w-sm relative">
                            <input 
                                type="file" 
                                accept=".csv" 
                                onChange={handleFileChange}
                                className="block w-full text-[10px] text-gray-400
                                file:mr-4 file:py-2 file:px-6
                                file:rounded-full file:border-0
                                file:text-[10px] file:font-black file:uppercase file:tracking-widest
                                file:bg-white/5 file:text-purple-400
                                hover:file:bg-purple-500/10 file:cursor-pointer mb-8 transition-all"
                            />
                        </div>

                        <motion.button
                            whileHover={{ scale: 1.02 }}
                            whileTap={{ scale: 0.98 }}
                            onClick={handleUpload}
                            disabled={loading || !file}
                            className="w-full md:w-64 bg-linear-to-r from-blue-600 to-purple-600 py-4 rounded-2xl font-black text-xs tracking-[0.2em] transition-all disabled:opacity-30 shadow-xl shadow-blue-500/20 flex justify-center items-center gap-2 uppercase"
                        >
                            {loading ? (
                                <Loader2 className="animate-spin" size={16} />
                            ) : (
                                <>{t("batch.btn_start")} <Zap size={16} /></>
                            )}
                        </motion.button>

                        {error && (
                            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="mt-6 text-red-400 text-[10px] font-black uppercase tracking-widest flex items-center gap-2 bg-red-500/10 px-4 py-2 rounded-lg border border-red-500/20">
                                <AlertCircle size={14}/> {error}
                            </motion.div>
                        )}
                    </div>
                </div>
            </div>

            {results.length > 0 && (
                <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="space-y-6">
                    <div className="flex items-center gap-3">
                        <Activity className="text-purple-500" size={20} />
                        <h2 className="text-lg font-black uppercase tracking-tight">{t("batch.results_title")}</h2>
                    </div>

                    <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                        {[
                            { label: t('stats.total'), val: stats.total, color: 'text-blue-400', bg: 'bg-blue-500/5' },
                            { label: t('stats.pos'), val: stats.pos, color: 'text-emerald-400', bg: 'bg-emerald-500/5' },
                            { label: t('stats.neg'), val: stats.neg, color: 'text-red-400', bg: 'bg-red-500/5' },
                            { label: t('stats.neu'), val: stats.neu, color: 'text-amber-400', bg: 'bg-amber-500/5' }
                        ].map((s, i) => (
                            <div key={i} className={`${s.bg} border border-white/5 p-4 rounded-2xl backdrop-blur-sm`}>
                                <p className="text-[9px] font-black text-gray-500 uppercase tracking-widest mb-1">{s.label}</p>
                                <p className={`text-2xl font-black ${s.color}`}>{s.val}</p>
                            </div>
                        ))}
                    </div>

                    <div className="bg-[#130b1d]/40 border border-white/5 rounded-3xl overflow-hidden backdrop-blur-md">
                        <div className="overflow-x-auto">
                            <table className="min-w-full">
                                <thead className="bg-white/5 border-b border-white/5">
                                    <tr>
                                        <th className="px-6 py-4 text-left text-[9px] font-black text-gray-500 uppercase tracking-widest">{t("batch.table_content")}</th>
                                        <th className="px-6 py-4 text-left text-[9px] font-black text-gray-500 uppercase tracking-widest">{t("batch.table_prediction")}</th>
                                        <th className="px-6 py-4 text-left text-[9px] font-black text-gray-500 uppercase tracking-widest">{t("batch.table_confidence")}</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-white/5">
                                    {results.slice(0, 10).map((res, index) => (
                                        <tr key={index} className="hover:bg-white/5 transition-colors group">
                                            <td className="px-6 py-4">
                                                <p className="text-xs text-gray-400 font-medium truncate max-w-[200px] md:max-w-xs italic">
                                                    "{res.original_text || "..."}"
                                                </p>
                                            </td>
                                            <td className="px-6 py-4">
                                                <span className={`px-3 py-1 rounded-lg text-[9px] font-black uppercase tracking-widest border ${
                                                    res.prediction?.toLowerCase().includes('pos') ? 'border-emerald-500/30 text-emerald-400 bg-emerald-500/5' :
                                                    res.prediction?.toLowerCase().includes('neg') ? 'border-red-500/30 text-red-400 bg-red-500/5' :
                                                    'border-amber-500/30 text-amber-400 bg-amber-500/5'
                                                }`}>
                                                    {res.prediction}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <p className="text-xs font-mono font-bold text-purple-400">
                                                    {(res.probability * 100).toFixed(1)}%
                                                </p>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {results.length > 10 && (
                            <div className="p-4 text-center border-t border-white/5">
                                <p className="text-[9px] text-gray-600 font-black uppercase tracking-[0.2em]">
                                    {t("batch.more_entries", { count: results.length - 10 })}
                                </p>
                            </div>
                        )}
                    </div>
                </motion.div>
            )}
        </motion.div>
    );
};

export default BatchAnalysis;