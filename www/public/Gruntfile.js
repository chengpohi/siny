module.exports = function(grunt) {
    grunt.initConfig({
	pkg: grunt.file.readJSON('package.json'),
	project: {
            app: ['coolmarks'],
            assets: ['<%= project.app %>/assets'],
            css: ['<%= project.assets %>/sass/style.scss']
	},
	sass: {
	    dev: {
		options: {
		    style: 'expanded',
		    compass: false
		},
		files: {
		    '<%= project.assets %>/css/style.css':'<%= project.css %>'
		}
	    }
	},
	watch: {
	    sass: {
		files: '<%= project.assets %>/sass/{,*/}*.{scss,sass}',
		tasks: ['sass:dev']
	    }
	},
	concat: {
            options: {
		separator: ';',
            },
            dist: {
		src: ['bower_components/angular/angular.js', 'bower_components/jquery/dist/jquery.js ', 'bower_components/bootstrap-sass-official/assets/javascripts/bootstrap.min.js', "bower_components/angular-local-storage/dist/angular-local-storage.js"],
		dest: '<%= project.assets %>/js/app.js',
            },
	},
    });

    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.registerTask('default', ['concat', 'watch']);
};

