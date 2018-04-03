flashXBlock
=========

### Description ###

This XBlock provides an easy way to embed a Flash (.swf) file.

- Download button available
- (Optional) Source document download button, for example to provide another format

### Customize the XBlock ###

- By default, Flash Download Allowed is set on True. The default value can  be changed in `flashXBlock / flash / flash.py`

### Install / Update the XBlock ###

    # Move to the folder where you want to download the XBlock
    cd /edx/app/edxapp
    # Download the XBlock
    sudo -u edxapp git clone https://github.com/MarCnu/flashXBlock.git
    # Install the XBlock
    sudo -u edxapp /edx/bin/pip.edxapp install flashXBlock/
    # Upgrade the XBlock if it is already installed, using --upgrade
    sudo -u edxapp /edx/bin/pip.edxapp install flashXBlock/ --upgrade
    # Remove the installation files
    sudo rm -r flashXBlock

### Reboot if something isn't right ###

    sudo /edx/bin/supervisorctl -c /edx/etc/supervisord.conf restart edxapp:

### Activate the XBlock in your course ###
Go to `Settings -> Advanced Settings` and set `advanced_modules` to `["flash"]`.

### Use the XBlock in a unit ###
Select `Advanced -> Flash` in your unit.
