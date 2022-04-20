
```
#
# install packages
#

#
# https://github.com/ohmyzsh/ohmyzsh
#
sh -c "$(wget -O- https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

#
# sudo first?
#
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

brew install bash

brew install wget

brew install --cask sublime-text

brew install alt-tab
brew install telnet


brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk

brew install --cask springtoolsuite
brew install --cask visual-studio-code

****

#
# csap demo
#

docker run \
	--rm \
	--detach \
	--name csap-demo \
	--publish 9011:9011 --publish 9013:9013 --publish 9021:9021  --publish 9023:9023 \
	--env dockerHostFqdn=$(hostname -f) \
	csapplatform/demo:latest
```
