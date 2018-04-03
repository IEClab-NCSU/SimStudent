""" flashXBlock main Python class"""

import pkg_resources
from django.template import Context, Template

from xblock.core import XBlock
from xblock.fields import Scope, Integer, String, Boolean
from xblock.fragment import Fragment

class flashXBlock(XBlock):

    '''
    Icon of the XBlock. Values : [other (default), video, problem]
    '''
    icon_class = "other"

    '''
    Fields
    '''
    display_name = String(display_name="Display Name",
        default="Flash",
        scope=Scope.settings,
        help="This name appears in the horizontal navigation at the top of the page.")

    url = String(display_name="Flash file URL (.swf)",
        default="",
        scope=Scope.content,
        help="The URL for your flash file.")
    
    allow_download = Boolean(display_name="Flash Download Allowed",
        default=True,
        scope=Scope.content,
        help="Display a download button for this flash file.")
    
    source_text = String(display_name="Source document button text",
        default="",
        scope=Scope.content,
        help="Add a download link for the source document of your flash file. Use it for example to provide another format.")
    
    source_url = String(display_name="Source document URL",
        default="",
        scope=Scope.content,
        help="Add a download link for the source document of your flash file. Use it for example to provide another format.")

    '''
    Util functions
    '''
    def load_resource(self, resource_path):
        """
        Gets the content of a resource
        """
        resource_content = pkg_resources.resource_string(__name__, resource_path)
        return unicode(resource_content)

    def render_template(self, template_path, context={}):
        """
        Evaluate a template by resource path, applying the provided context
        """
        template_str = self.load_resource(template_path)
        return Template(template_str).render(Context(context))

    '''
    Main functions
    '''
    def student_view(self, context=None):
        """
        The primary view of the XBlock, shown to students
        when viewing courses.
        """
        
        context = {
            'display_name': self.display_name,
            'url': self.url,
            'allow_download': self.allow_download,
            'source_text': self.source_text,
            'source_url': self.source_url
        }
        html = self.render_template('static/html/flash_view.html', context)
        
        frag = Fragment(html)
        frag.add_css(self.load_resource("static/css/flash.css"))
        frag.add_javascript(self.load_resource("static/js/flash_view.js"))
        frag.initialize_js('flashXBlockInitView')
        return frag

    def studio_view(self, context=None):
        """
        The secondary view of the XBlock, shown to teachers
        when editing the XBlock.
        """
        context = {
            'display_name': self.display_name,
            'url': self.url,
            'allow_download': self.allow_download,
            'source_text': self.source_text,
            'source_url': self.source_url
        }
        html = self.render_template('static/html/flash_edit.html', context)
        
        frag = Fragment(html)
        frag.add_javascript(self.load_resource("static/js/flash_edit.js"))
        frag.initialize_js('flashXBlockInitEdit')
        return frag

    @XBlock.json_handler
    def save_flash(self, data, suffix=''):
        """
        The saving handler.
        """
        self.display_name = data['display_name']
        self.url = data['url']
        self.allow_download = True if data['allow_download'] == "True" else False # Str to Bool translation
        self.source_text = data['source_text']
        self.source_url = data['source_url']
        
        return {
            'result': 'success',
        }
