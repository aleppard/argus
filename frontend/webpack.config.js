const path = require('path');

const mode = process.env.NODE_ENV ?? 'development';

module.exports = {
    entry: './src/index.js',
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: ['babel-loader'],
            },
            {
                test: /\.svg$/,
                use: ['@svgr/webpack'],
            }
        ],
      },
    resolve: {
        extensions: ['*', '.js', '.jsx'],
    },
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js',
    },
    mode: mode
};
