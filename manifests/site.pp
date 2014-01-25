$sbt_url = "http://repo.scala-sbt.org/scalasbt/sbt-native-packages/org/scala-sbt/sbt/0.13.0/sbt.deb"

exec { "apt-update":
    command => "/usr/bin/apt-get update"
}

Exec["apt-update"] -> Package <| |>

package { "openjdk-7-jdk":
    ensure => "installed",
}

package { "curl":
    ensure => "installed",
}

package { "mysql-server":
    ensure => "installed",
}

exec { "sbt.deb":
    command => "/usr/bin/curl -O $sbt_url",
    cwd     => "/home/vagrant",
    creates => "/home/vagrant/sbt.deb",
    require => [
        Package["curl"],
        Package["openjdk-7-jdk"],
    ],
}

package { "sbt.db":
    ensure => installed,
    provider => dpkg,
    source => "/home/vagrant/sbt.deb",
    require => Exec["sbt.deb"],
}

exec { "createdatabase":
    command => "/usr/bin/mysql -uroot < createdatabase.sql",
    cwd     => "/vagrant",
    require => Package["mysql-server"],
}
