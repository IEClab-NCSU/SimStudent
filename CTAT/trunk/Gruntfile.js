/**
 * @fileoverview Sets up and executes tasks using grunt @link http://gruntjs.com/
 * @author $Author: mringenb $
 * @version $Revision: 21634 $
 */

/**
 * Remove the beep, RIP ears
 */
var oldout = process.stdout.write;
process.stdout.write = function(msg)
{
	oldout.call(this, msg.replace('\x07', ''));
};

/*
 * $Date$
 * $HeadURL$

 -
 License:
 -
 ChangeLog:
 -
 Notes:
	To setup and use this, check out the instructions at
	http://gruntjs.com/getting-started

	Quick Instructions:
	1. $ npm update -g npm
	2. $ npm install -g grunt-cli
	3. $ npm install
	4. $ grunt

	If any closure-compiler builds fail and it complains that it is unable to
	access a jarfile, try running: $ npm install

	Targets: `grunt --help` will list all the builds
 * ctat (default): builds ctat.min.js
 * ctat_full: builds ctat.full.js (non-compiled version of ctat.min.js)
 * tutor: builds the tutor interface bundle.
 * sidebar: builds the tutor interface bundle for Google's side bar.
 * tracer: builds the CTAT tracer bundle.
 * dep: builds the dependency file necessary to run straight off the source code.
 * test: run unit tests.
 * deploy: copy necessary files for running a site to ./dist/
 * deploy_dev: copy necessary src files for running a site to ./dist-dev/
 */
module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'), // read settings from package
		dir: { // commonly referenced directories
			src: 'src',
			output: 'ctatjslib',
			deploy: 'dist',
			dev: 'dist-dev',
			gen: 'gen',
			dep: 'CTAT-deps.js',
			tracer: '<%= dir.src %>/CTATExampleTracer',
			tracer_gen: '<%= dir.gen %>/CTATExampleTracer',
			rule_tracer: '<%= dir.src %>/CTATRuleTracer',
			node_modules: 'node_modules',
			closure_compiler: '<%= dir.node_modules %>/google-closure-compiler',
			closure_library: '<%= dir.node_modules %>/google-closure-library',
		},
		cmd: { // location of the depswriter script
			depswriter: '<%= dir.closure_library %>/closure/bin/build/depswriter.py'
		},
		lib: {
			jquery: '<%= dir.node_modules %>/jquery/dist/jquery.js',
			jstz: ['<%= dir.node_modules %>/jstz/jstz.main.js','<%= dir.node_modules %>/jstz/jstz.rules.js'],
			spin_js: '<%= dir.node_modules %>/spin.js/spin.js',
			x2js: '<%= dir.node_modules %>/x2js/xml2json.min.js'
		},
		lib_min: {
			jquery: '<%= dir.node_modules %>/jquery/dist/jquery.min.js',
			jstz: '<%= dir.node_modules %>/jstz/dist/jstz.min.js',
			spin_js: '<%= dir.node_modules %>/spin.js/spin.min.js',
			x2js: '<%= dir.node_modules %>/x2js/xml2json.min.js'
		},
		source: { // Source code listings
			CTAT: '<%= dir.src %>/CTAT/**.js',
			Assistments: '<%= dir.src %>/Assistments/**.js',
			CTATCommunication: '<%= dir.src %>/CTATCommunication/**.js',
			CTATComponentHierarchy:' <%= dir.src %>/CTATComponentHierarchy/**.js',
			CTATComponents: ['<%= dir.src %>/CTATComponents/**.js',
			                 '!<%= dir.src %>/CTATComponent/CTATAudioButtonImaged.js',
			                 '!<%= dir.src %>/CTATComponents/CTATWorkedExamplePlayer.js'],
			CTATMath: '<%= dir.src %>/CTATMath/**.js',
			CTATSerialization: '<%= dir.src %>/CTATSerialization/**.js',
			CTATUtil: '<%= dir.src %>/CTATUtil/**.js',
			base: [//'<%= dir.src %>/CTATBinaryImages.js',
			       '<%= dir.src %>/CTATCanvasComponent.js',
			       '<%= dir.src %>/CTATContextData.js',
			       '<%= dir.src %>/CTATCSS.js',
			       //'<%= dir.src %>/DateTime.js',
			       '<%= dir.src %>/CTATDialogBox.js',
			       //'<%= dir.src %>/CTATExampleTracer_Skills.js',
			       //'<%= dir.src %>/CTATExampleTracer.js',
			       //'<%= dir.src %>/CTATExternals.js',
			       '<%= dir.src %>/CTATConfiguration.js',
			       '<%= dir.src %>/CTATFlashVars.js',
			       '<%= dir.src %>/CTATGlobalFunctions.js',
			       '<%= dir.src %>/CTATGlobals.js',
			       '<%= dir.src %>/CTATGraphicsTools.js',
			       '<%= dir.src %>/CTATHTMLManager.js',
			       '<%= dir.src %>/CTATLinkData.js',
			       '<%= dir.src %>/CTATLMS.js',
			       '<%= dir.src %>/CTATMobileTutorHandler.js',
			       '<%= dir.src %>/CTATScrim.js',
			       '<%= dir.src %>/CTATShape.js',
			       '<%= dir.src %>/CTATStringUtil.js',
			       '<%= dir.src %>/CTATStyle.js',
			       //'<%= dir.src %>/CTATTools.js',
			       '<%= dir.src %>/TutorShopAPIProxy.js',
			       '<%= dir.src %>/CTATEnabledInPreview.js'],
			tutor: ['<%= dir.src %>/CTATProblem.js',
					'<%= dir.src %>/CTATProblemSet.js',
					'<%= dir.src %>/CTATPackage.js',
					'<%= dir.src %>/CTATSequencer.js',
			        '<%= dir.src %>/CTATTutor.js',
			        '<%= source.base %>',
			        '<%= source.CTAT %>',
			        '<%= source.CTATCommunication %>',
			        '<%= source.CTATComponentHierarchy %>',
			        '<%= source.CTATComponents %>',
			        '<%= source.CTATMath %>',
			        '<%= source.CTATSerialization %>',
					'<%= source.Assistments %>',
			        '<%= source.CTATUtil %>'],
			logging: ['<%= dir.src %>/CTATBinaryImages.js',
			       '<%= dir.src %>/CTATCanvasComponent.js',
			       //'<%= dir.src %>/CTATTools.js', //unused
			       '<%= dir.src %>/TutorShopAPIProxy.js'],
			sidebar: ['<%= source.tutor %>',
			          '<%= dir.src %>/CTATBinaryImages.js',
			          '<%= dir.src %>/CTATComponents/CTATWorkedExamplePlayer.js',
			          '!<%= dir.src %>/CTATTutor.js',
			          '!<%= dir.src %>/CTATComponents/CTATNumberLine.js',
			          '!<%= dir.src %>/CTATComponents/CTATPieChart.js',
			          '!<%= dir.src %>/CTATComponents/CTATFractionBar.js',
			          '!<%= dir.src %>/CTATComponentHierarchy/SVGButton.js',
			          '!<%= dir.src %>/CTATComponentHierarchy/CTATUnitDisplayComponent.js',
			          '!<%= dir.src %>/CTATComponentHierarchy/CTATSVGComponent.js',
			          '!<%= dir.src %>/CTATMath/Point.js',
			          '!<%= dir.src %>/CTATMath/Rectangle.js',
			          '<%= dir.src %>/CTATTutorGoogleSideBar.js'],
			tracer: ['<%= source.tutor %>',
			         //'<%= dir.src %>/polyfills.js',
			         '<%= dir.src %>/sprintf.js',
			         '<%= dir.src %>/CTATExampleTracer.js',
			         '<%= dir.tracer %>/CTATHintPolicyEnum.js',
			         '<%= dir.tracer %>/CTATExampleTracerException.js',
			         '<%= dir.tracer %>/CTATMsgType.js',
			         '<%= dir.tracer %>/CTATVariableTable.js',
			         '<%= dir.tracer %>/CTATExampleTracerLink.js',
			         '<%= dir.tracer %>/CTATLinkGroup.js',
			         '<%= dir.tracer %>/CTATVersionComparator.js',
			         '<%= dir.tracer %>/CTATExampleTracerGraphVisualData.js',
			         '<%= dir.tracer %>/CTATExampleTracerLinkVisualData.js',
			         '<%= dir.tracer %>/CTATExampleTracerNodeVisualData.js',
			         '<%= dir.tracer %>/CTATExampleTracerTracer.js',
			         '<%= dir.tracer %>/CTATMatcher.js',
			         '<%= dir.tracer %>/CTATAnyMatcher.js',
			         '<%= dir.tracer %>/CTATExactMatcher.js',
			         '<%= dir.tracer %>/CTATRegexMatcher.js',
			         '<%= dir.tracer %>/CTATMatcherComparator.js',
			         '<%= dir.tracer %>/CTATSingleMatcher.js',
			         '<%= dir.tracer %>/CTATExampleTracerSkill.js',
			         '<%= dir.tracer %>/CTATSkills.js',
			         '<%= dir.tracer %>/CTATDefaultLinkGroup.js',
			         '<%= dir.tracer %>/CTATExampleTracerNode.js',
			         '<%= dir.tracer %>/CTATExampleTracerPathComparator.js',
			         '<%= dir.tracer %>/CTATExampleTracerEvent.js',
			         '<%= dir.tracer %>/CTATExpressionMatcher.js',
			         '<%= dir.tracer %>/CTATGroupIterator.js',
			         '<%= dir.tracer %>/CTATGroupModel.js',
			         '<%= dir.tracer %>/CTATRangeMatcher.js',
			         '<%= dir.tracer %>/CTATExampleTracerPath.js',
			         '<%= dir.tracer %>/CTATProblemSummary.js',
			         '<%= dir.tracer %>/CTATStep.js',
			         '<%= dir.tracer %>/CTATWildcardMatcher.js',
			         '<%= dir.tracer %>/CTATExampleTracerInterpretationComparator.js',
			         '<%= dir.tracer %>/CTATExampleTracerLinkComparator.js',
			         '<%= dir.tracer %>/CTATVectorMatcher.js',
			         '<%= dir.tracer %>/CTATExampleTracerInterpretation.js',
			         '<%= dir.tracer %>/CTATDefaultGroupModel.js',
			         '<%= dir.tracer %>/CTATExampleTracerGraph.js',
			         '<%= dir.tracer %>/CTATMessageTank.js',
			         '<%= dir.tracer %>/CTATGraphParser.js',
			         '<%= dir.tracer %>/CTATProblemStateStatus.js',
			         '<%= dir.tracer %>/ProblemStateSaver.js',
			         '<%= dir.tracer %>/ProblemStateRestorer.js',
			         '<%= dir.tracer %>/SCORMProblemSummary.js',
			         '<%= dir.tracer_gen %>/CTATFormulaParser/CTATJSFormulaParser.js',
			         '<%= dir.tracer_gen %>/CTATFormulaParser/CTATFormulaFunctions.js',
			         '<%= dir.tracer_gen %>/CTATFormulaParser/CTATFormulaActions.js',
			         // '<%= dir.tracer_gen %>/CTATFormulaParser/CTATFormulaParser.js',
			         // '<%= dir.tracer_gen %>/CTATFormulaParser/CTATFormulaGrammar.js',
			         // '<%= dir.tracer_gen %>/CTATFormulaParser/CTATFormulaTree.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATAlgebraParser.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATAlgebraGrammar.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATRelationNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATAdditionNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATMultiplicationNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATIntDivisionNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATUnaryNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATPowerNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATVariableNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATConstantNode.js',
			         '<%= dir.tracer_gen %>/CTATAlgebraParser/CTATTreeNode.js',
				 '<%= dir.rule_tracer %>/CTATNoolsTracer.js',
				 '<%= dir.rule_tracer %>/CTATRuleTracer.js',
				 '<%= dir.rule_tracer %>/CTATNoolsTracerUtil.js',
				 '<%= dir.rule_tracer %>/CTATNoolsSessionManager.js',
				 '<%= dir.rule_tracer %>/CTATLogger.js']
		}});

	/**
	 * Google closure compiler for compiling and minimizing files into a
	 * single result file.
	 */
	var gcc = require('google-closure-compiler'); gcc.grunt(grunt);
	grunt.config('closure-compiler', {
		options: {
			define: "\"version='<%= pkg.version %>'\""
		},
		ctat_advanced: { // using advanced compilation, currently does not produce usable file.
			files: {
				'<%= dir.output %>/<%= pkg.name %>.min.js':
					['<%= dir.closure_library %>/closure/goog/base.js',
					 //'<%= lib_min.jquery %>',
					 '<%= lib_min.jstz %>',
					 '<%= lib_min.spin_js %>',
					 '<%= source.tracer %>']
			},
			options: {
				entry_point: ['CTATTutor','CTATExampleTracer'],
				compilation_level: 'ADVANCED',
				create_source_map: '<%= dir.output %>/<%= pkg.name %>.min.js.map',
				output_wrapper: "%output%\n//# sourceMappingURL=<%= pkg.name %>.min.js.map",
				dependency_mode: 'STRICT',
				generate_exports: true,
				externs: [gcc.compiler.CONTRIB_PATH+'/externs/jquery-1.9.js',
				          'externs/DOMPoint.js', 'externs/CTATConfig.js', 'externs/jstz.js'],
				formatting: 'PRETTY_PRINT'
				//process_common_js_modules: true
				//process_jquery_primitives: true
			}
		},
		ctat: { // Main ctat.min.js build.
			files: {
				'<%= dir.output %>/<%= pkg.name %>.min.js':
					 ['<%= lib.jstz %>',
					  '<%= lib.spin_js %>',     
					  '<%= source.tracer %>']
			},
			options: {
				entry_point: ['CTATTutor','CTATExampleTracer'],
				compilation_level: 'SIMPLE',
				create_source_map: '<%= dir.output %>/<%= pkg.name %>.min.js.map',
				//externs: gcc.compiler.CONTRIB_PATH + '/externs/jquery-3.1.js',
				dependency_mode: 'LOOSE',
				//manage_closure_dependencies: true,
				output_manifest: 'ctat.MF',
				//output_wrapper: '(function(){\n%output%\n}).call(this);\n//# sourceMappingURL=<%=pkg.name%>.min.js.map'
				//process_common_js_modules: true,
				//transform_amd_modules: true,
				//js_module_root: 'node_modules',
				//language_out: 'ES5',
				//formatting: 'PRETTY_PRINT'
			}
		},
		ctat_editor: { //For inclusion in context of html editor.  Only difference is spin.js is left out
			files: {
				'<%= dir.output %>/<%= pkg.name %>_editor.min.js':
					 ['<%= lib.jstz %>',
					  '<%= source.tracer %>']
			},
			options: {
				entry_point: ['CTATTutor','CTATExampleTracer'],
				compilation_level: 'SIMPLE',
				create_source_map: '<%= dir.output %>/<%= pkg.name %>.min.js.map',
				dependency_mode: 'LOOSE',
			}

		},
		ctat_debug: { // compile under greater restriction to expose problems.
			files: {
				'<%= dir.output %>/<%= pkg.name %>.debug.min.js':
					 ['<%= lib_min.jstz %>',
					  '<%= lib_min.spin_js %>',
					  '<%= source.tracer %>']
			},
			options: {
				entry_point: ['CTATTutor','CTATExampleTracer'],
				compilation_level: 'SIMPLE',
				create_source_map: '<%= dir.output %>/<%= pkg.name %>.min.js.map',
				define: "\"useDebugging=true\"",
				debug: true,
				dependency_mode: 'LOOSE',
				formatting: 'PRETTY_PRINT',
				warning_level: 'VERBOSE',
				output_wrapper: "%output%\n//# sourceMappingURL=<%= pkg.name %>.min.js.map"
			}
		},
		ctat_full: { // Somewhat smarter concat build.
			files: {
				'<%= dir.output %>/<%= pkg.name %>.full.js':
					 ['<%= lib_min.jstz %>',
					  '<%= lib_min.spin_js %>',	
					  '<%= source.tracer %>']
			},
			options: {
				entry_point: ['CTATTutor','CTATExampleTracer'],
				compilation_level: 'WHITESPACE_ONLY',
				dependency_mode: 'LOOSE',
				formatting: 'PRETTY_PRINT'
			}
		},
		tutor: { // interface only
			files: {
				'<%= dir.output %>/<%= pkg.name %>-tutor.min.js':
					['<%= lib.jstz %>', '<%= lib.spin_js %>', '<%= source.tutor %>']
			},
			options: {
				entry_point: ['CTATTutor'],
				compilation_level: 'SIMPLE',
				dependency_mode: 'LOOSE',
			}
		},
		tutor_full: {
			files: {
				'<%= dir.output %>/<%= pkg.name %>-tutor.full.js':
					['<%= lib_min.jstz %>', '<%= lib_min.spin_js %>', '<%= source.tutor %>']
			},
			options: {
				entry_point: ['CTATTutor'],
				compilation_level: 'WHITESPACE_ONLY',
				dependency_mode: 'LOOSE',
			}

		},
		sidebar: {
			files: {
				'<%= dir.output %>/<%= pkg.name %>-tutor-sidebar.min.js':
					['<%= lib.jstz %>', '<%= lib.spin_js %>', '<%= source.sidebar %>']
			},
			options: {
				entry_point: ['CTATTutorGoogleSideBar'],
				compilation_level: 'SIMPLE',
				dependency_mode: 'LOOSE'
			}
		}
	});
	/**
	 * #Google Closure Tools:
	 *  Use Google's closure compiler to minify javascript code
	 *  or to generate the deps file needed to run from source.
	 */
	grunt.loadNpmTasks('grunt-closure-tools');
	grunt.config('closureDepsWriter', {
		options: {
			closureLibraryPath: '<%= dir.closure_library %>',
			depswriter: '<%= cmd.depswriter %>',
			root_with_prefix: ['"src ../../../../src"', // relative to closure-lib
			                   '"gen ../../../../gen"'], // relative to closure-lib
		},
		'CTAT-deps': {
			dest: '<%= dir.dep %>'
		}
	});

	/**
	 * #jsHint:
	 *  Run a basic javascript code checker.  Good idea to run this before
	 *  checking in source code.
	 */
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.config('jshint', { // basic source code checker
		options: { esnext: true },
		tutor: '<%= source.tutor %>',
		sidebar: '<%= source.sidebar %>',
		tracer: '<%= source.tracer %>'
	});

	/**
	 * #QUnit:
	 *  A unit testing service.
	 */
	grunt.loadNpmTasks('grunt-contrib-qunit');
	grunt.config('qunit', { // Unit tests
		CTATMath: ['test/CTATMath.html'],
		CTATGlobalFunctions: ['test/CTATGlobalFunctions.html'],
		CTATGeom: ['test/CTATGeom.html'],
		CTAT: ['test/CTAT.html']
	});


	/**
	 * #Coffee:
	 *  Generate .js files from .coffee files.
	 */
	grunt.loadNpmTasks('grunt-contrib-coffee');
	grunt.config('coffee', { // compile .coffee files to .js
		options: {
			//bare: true, // setting this to true "fixes" goog.require complaints in the closureCompiler for the generated code.
		},
		files: {
			expand: true,
			cwd: '<%= dir.src %>/',
			src: ['**/*.coffee'],
			dest: '<%= dir.gen %>/',
			ext: '.js'
		}
	});


	/**
	 * #Jison
	 *  Generate .js files from .jison files.
	 */
	grunt.loadNpmTasks('grunt-jison');
	grunt.config('jison', { // compile .jison files to .js
		files: { // moving %{ goog.provide... %} to the start of the jison files
			// should fix the problem with closureCompiler complaints, but there
			// is apparently a bug in jison that does not places this properly
			// in the resulting file.
			expand: true,
			cwd: '<%= dir.src %>/',
			src: ['**/*.jison'],
			dest: '<%= dir.gen %>/',
			ext: '.js'
		}
	});


	/**
	 * #Clean
	 *  Cleans up generated and derivative files.
	 */
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.config('clean', { // clean/remove output files
		build: '<%= dir.output %>/**',
		dep: '<%= dir.dep %>',
		dist: '<%= dir.deploy %>',
		dev: '<%= dir.dev %>',
		gen: '<%= dir.gen %>'
	});



	/**
	 * #trimtrailingspaces:
	 * Clean excess whitespace from files.  This should be used before each
	 * checkin.
	 */
	grunt.loadNpmTasks('grunt-trimtrailingspaces');
	grunt.config('trimtrailingspaces', { // clear extra whitespace from source code
		options: {filter: 'isFile', encoding: 'utf8', failIfTrimmed: false},
		tutor: {src: '<%= source.tutor %>'},
		sidebar: {src: '<%= source.sidebar %>'},
		tracer: {src: '<%= source.tracer %>'},
		tests: {src: ['test/**.js']},
		gruntfile: {src: 'Gruntfile.js'}
	});


	/**
	 * Copy:
	 *  Copy files to prescribed places
	 */
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.config('copy', {
		// dev targets
		devdep: {expand: true, src: ['<%= dir.dep %>'], dest: '<%= dir.dev %>/'},
		devsrc: {
			expand: true,
			src: ['<%= dir.src %>/**/*.js', '!<%= dir.src %>/Node/*.js', '!<%= dir.src %>/**/old_code/*.js'],
			dest: '<%= dir.dev %>/'
		},
		devgen: {
			expand: true,
			src: ['<%= dir.gen %>/**/*.js'],
			dest: '<%= dir.dev %>/'
		},
		// dist targets
		distcss: {expand: true, src: ['css/*.css'], dest: '<%= dir.deploy %>/'},
		distsrc: {
			expand: true,
			src: ['<%= dir.tracer %>/CTATForBrowsers.js', '<%= dir.src %>/goog_stub.js'],
			dest: '<%= dir.deploy %>/ctatjslib/',
			flatten: true
		},
		distctatlib: {expand: true, src: ['<%= dir.output %>/**.js'], dest: '<%= dir.deploy %>/'},
		distlib: {
			expand: true,
			src: ['node_modules/jquery/dist/jquery*', 'node_modules/jquery-ui/jquery*',
			      '<%= lib.jstz %>', '<%= lib_min.jstz %>',
			      '<%= lib.spin_js %>', '<%= lib_min.spin_js %>'],
			dest: '<%= dir.deploy %>/lib/',
			flatten: true,
			filter: 'isFile'
		},
		distroot: {
			expand: true,
			src: ['*.png', '*.html'],
			dest: '<%= dir.deploy %>/',
			options: {
				process: function (content, srcpath) {
					// change node_module... or third-party... svg.js to lib/svg.js
					return content.replace(/".*\/(svg.*\.js)"/, "\"lib/$1\"");
				}
			}
		}
	});


	/**
	 * #Watch:
	 *  Runs a persistant task that will execute tasks when one of the listed
	 *  files changes.  For example, recompile the minified files when one of
	 *  the source files change.
	 */
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.config('watch', {
		ctat: {
			files: ['<%= source.tracer %>'],
			tasks: ['ctat']
		},
		tutor: {
			files: ['<%= source.tutor %>'],
			tasks: ['tutor']
		},
		sidebar: {
			files: ['<%= source.sidebar %>'],
			tasks: ['sidebar']
		},
		cssmin: {
			files: ['css/CTAT.css'],
			tasks: ['cssmin']
		}
	});


	/**
	 * #listfiles:
	 *  Generate manifest files based on grunt templates.
	 */
	grunt.loadNpmTasks('grunt-listfiles');
	grunt.config('listfiles', { // generate a manifest file for each of the builds.
		options: {}, // see https://www.npmjs.com/package/grunt-listfiles for options.
		ctat: {files: {'ctat.mf': ['<%= lib.jstz %>', '<%= lib.spin_js %>', '<%= source.tracer %>']}},
		tutor: {files: {'tutor.mf': ['<%= lib.jstz %>', '<%= lib.spin_js %>', '<%= source.tutor %>']}},
		sidebar: {files: {'sidebar.mf': ['<%= lib.jstz %>', '<%= lib.spin_js %>', '<%= source.sidebar %>']}}
	});


	/**
	 * #fix_require
	 *  Moves goog.provide|require statements to the top of a file.
	 *  This replaces shell:fix_coffee as a pure JavaScript solution not dependent
	 *  on sh and sed.
	 */
	grunt.config('fix_require', {
		gen: {
			src: ['<%= dir.tracer_gen %>/CTATFormulaParser/**.js', '<%= dir.tracer_gen %>/CTATAlgebraParser/**.js']
		}
	});
	grunt.registerMultiTask('fix_require',
			'Move goog.provide and goog.require statements to the top of a file.',
			// note, this task only needs src: and is destructive.
			function() {
		//var done = this.async();
		//var i=0;
		var gpr = /goog\.(provide|require)\(.*\);.*[\n\r]+/g;
		this.files.forEach(function(file) {
			grunt.log.writeln('Processing '+ file.src.length + ' files.');
			file.src.forEach(function(f) {
				//grunt.verbose.write(' Processing '+f+' ');
				var contents = grunt.file.read(f);
				var req = contents.match(gpr);
				if (req) {
					grunt.log.debug('  Matches:\n   '+req.join('   '));
					contents = req.join('') +contents.replace(gpr,'');
					grunt.file.write(f,contents);
					grunt.verbose.ok();
				} else {
					grunt.verbose.error('WARNING: No goog.require|provide statements!');
				}
			});
			//if(i >= file.src.length) done(true);
		});
	});

	/**
	 * CSS minimization
	 */
	grunt.loadNpmTasks('grunt-contrib-cssmin');
	grunt.config('cssmin', {
		ctat: {
			files: {
				'css/CTAT.min.css': ['css/CTAT.css']
			}
		}
	})

	// Adds the meta-task newer which conditionally execute the task only if
	// any of the src files are newer than the dest files.
	grunt.loadNpmTasks('grunt-newer');

	// Targets
	grunt.registerTask('parser', ['newer:coffee', 'newer:jison', 'newer:fix_require']);
	grunt.registerTask('ctat_min', ['parser', 'newer:closure-compiler:ctat_advanced']);
	grunt.registerTask('ctat', ['parser', 'newer:closure-compiler:ctat']);
	grunt.registerTask('ctat_editor', ['parser', 'newer:closure-compiler:ctat_editor']);
	grunt.registerTask('ctat_full', ['parser', 'closure-compiler:ctat_full']);
	grunt.registerTask('tutor_full', ['closure-compiler:tutor_full']);
	grunt.registerTask('tutor', ['newer:closure-compiler:tutor']);
	grunt.registerTask('sidebar', ['newer:closure-compiler:sidebar']);
	// default task.  Used when grunt is called with no target
	grunt.registerTask('default', ['ctat', 'ctat_editor']);
	grunt.registerTask('test', ['dep', 'qunit']);
	grunt.registerTask('dep', ['parser', 'closureDepsWriter']);
	grunt.registerTask('copydist', ['copy:distcss', 'copy:distsrc', 'copy:distctatlib', 'copy:distlib', 'copy:distroot']);
	grunt.registerTask('copydev', ['copy:devdep', 'copy:devsrc', 'copy:devgen']);
	grunt.registerTask('deploy', ['clean:dist', 'parser', 'default', 'copydist']);
	grunt.registerTask('deploy_dev', ['clean:dep', 'parser', 'dep', 'clean:dev', 'copydev']);
	grunt.registerTask('brew', ['fix_require']);
};
