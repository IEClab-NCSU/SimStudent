"""Setup for flashXBlock."""

import os
from setuptools import setup


def package_data(pkg, root):
    """Generic function to find package_data for `pkg` under `root`."""
    data = []
    for dirname, _, files in os.walk(os.path.join(pkg, root)):
        for fname in files:
            data.append(os.path.relpath(os.path.join(dirname, fname), pkg))

    return {pkg: data}


setup(
    name='flash-xblock',
    version='0.1',
    description='This XBlock provides an easy way to embed a Flash file.',
    packages=[
        'flash',
    ],
    install_requires=[
        'XBlock',
    ],
    entry_points={
        'xblock.v1': [
            'flash = flash:flashXBlock',
        ]
    },
    package_data=package_data("flash", "static"),
)