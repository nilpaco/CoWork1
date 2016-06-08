# CoWork

This application was generated using JHipster, you can find documentation and help at [https://jhipster.github.io](https://jhipster.github.io).

####Before you can build this project, you must install and configure the following dependencies on your machine:
######1. Java: We use Java to run the project.

    sudo echo 'deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list
    sudo echo 'deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list
    sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886
    
    sudo echo oracle-java-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
    sudo apt-get install -y --force-yes oracle-java8-installer
    sudo update-java-alternatives -s java-8-oracle
    
######2. Maven: We use Maven to run the project and download the dependencies.
    sudo curl -fsSL http://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz | sudo tar xzf - -C /usr/share && sudo mv /usr/share/apache-maven-3.3.9 /usr/share/maven && sudo ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

######3. MySQL: We use MySQL to store the data from our project.
    sudo apt-get install mysql-server
    
######4. GIT: We use GIT to download the source code.
    sudo apt-get install git

######5. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
   
    sudo apt-get install nodejs

After installing Node, you should be able to run the following command to install development tools (like
[Bower][] and [BrowserSync][]). You will only need to run this command when dependencies change in package.json.

    npm install
    
######6. NPM, Bower & Gulp: for managing CSS and JS dependencies and code compilation.

    sudo apt-get install npm
    npm install -g bower
    npm install -g gulp-cli

####Deployment:

Create and go inside a folder that is going to store the source code of the application, we can use the following command:

    mkdir path
    cd /path/


Donwload the source code using this command:

    git clone https://github.com/nilpaco/CoWork1.git
    
Now we can run the project using the following command:

    cd /path/appname
    mvn spring-boot:run

# Building for production

To optimize the project1 client for production, run:

    mvn -Pprod clean package

This will concatenate and minify CSS and JavaScript files. It will also modify `index.html` so it references
these new files.

To ensure everything worked, run:

    java -jar target/*.war --spring.profiles.active=prod

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

# Testing

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in `src/test/javascript` and can be run with:

    gulp test



# Continuous Integration

To setup this project in Jenkins, use the following configuration:

* Project name: `project1`
* Source Code Management
    * Git Repository: `git@github.com:xxxx/project1.git`
    * Branches to build: `*/master`
    * Additional Behaviours: `Wipe out repository & force clone`
* Build Triggers
    * Poll SCM / Schedule: `H/5 * * * *`
* Build
    * Invoke Maven / Tasks: `-Pprod clean package`
* Post-build Actions
    * Publish JUnit test result report / Test Report XMLs: `build/test-results/*.xml`

[JHipster]: https://jhipster.github.io/
[Node.js]: https://nodejs.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
