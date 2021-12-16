
```
#
# install packages
#

/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

brew install wget

brew install --cask sublime-text


brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk.jdk

brew install --cask springtoolsuite


#
# csap demo
#

docker run \
	--rm \
	--name csap-demo \
	--publish 9011:9011 --publish 9013:9013 --publish 9021:9021  --publish 9023:9023 \
	--env dockerHostFqdn=$(hostname -f) \
	csapplatform/demo:latest
```