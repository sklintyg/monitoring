var path = require('path');

module.exports = function(grunt) {
    'use strict';

    grunt.loadNpmTasks('grunt-contrib-csslint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-ng-annotate');
    grunt.loadNpmTasks('grunt-concurrent');

    var SRC_DIR = 'src/main/webapp/js/';
    var TEST_DIR = 'src/test/js/';

    var monitoring = grunt.file.readJSON(SRC_DIR + 'app-deps.json').map(function(file) {
        return file.replace(/\/js\//g, SRC_DIR);
    });

    monitoring = [SRC_DIR + 'monitor.js'].concat(monitoring);

    var COMMON_DIR = '/../../common/web/src/main/resources/META-INF/resources/webjars/common';

    grunt.initConfig({

        csslint: {
            monitoring: {
                options: {
                    csslintrc: '../target/build-tools/csslint/.csslintrc',
                    force: true
                },
                src: [SRC_DIR + '../**/*.css']
            }
        },

        concat: {
            monitoring: {
                src: monitoring,
                dest: SRC_DIR + 'app.min.js'
            }
        },

        jshint: {
            monitoring: {
                options: {
                    jshintrc: '../target/build-tools/jshint/.jshintrc',
                    force: true
                },
                src: ['Gruntfile.js', SRC_DIR + '**/*.js', TEST_DIR + '**/*.js', '!' + SRC_DIR + '/app.min.js']
            }
        },

        ngAnnotate: {
            options: {
                singleQuotes: true
            },
            monitoring: {
                src: SRC_DIR + 'app.min.js',
                dest: SRC_DIR + 'app.min.js'
            }
        },

        uglify: {
            options: {
                mangle: false
            },
            monitoring: {
                src: SRC_DIR + 'app.min.js',
                dest: SRC_DIR + 'app.min.js'
            }
        }
    });

    grunt.registerTask('build', ['concat', 'ngAnnotate', 'uglify']);
    grunt.registerTask('lint', ['jshint', 'csslint']);
};
