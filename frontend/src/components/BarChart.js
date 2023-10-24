import React from 'react';
import { Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    BarElement,
    Title,
    Tooltip,
    Legend,
    Filler,
} from 'chart.js';

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    BarElement,
    Title,
    Tooltip,
    Legend,
    Filler
);

export default function Bars({ data, label, max, color }) {

    const sortedData = Object.entries(data).sort(([a], [b]) => a - b);
    const labels = sortedData.map(([key]) => key);
    const values = sortedData.map(([_, value]) => value);

    var misoptions = {
        responsive: true,
        animation: false,
        plugins: {
            legend: {
                display: true,
                labels: {
                    color: 'white'
                }
            }
        },
        scales: {
            y: {
                min: 0,
                max: max,
                ticks: { color: 'white' },
                grid: {
                    color: 'rgb(157,187,191,0.4)'
                }

            },
            x: {
                ticks: { color: 'white' },

            }
        },
    };

    var midata = {
        labels: labels,
        datasets: [
            {
                label: label,
                data: values,
                backgroundColor: color
            }
        ]
    };

    return <Bar data={midata} options={misoptions} />;
}
