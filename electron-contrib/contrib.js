const fs = require('fs');
const exec = require('child_process').exec;

module.exports = {
  check: function(directory, callback) {
    return check(directory, callback);
  }
};

function check(directory, callback) {
  checkInt(directory, directory, callback);
}

function checkInt(gitRepoPath, currentDirectory, callback) {
  fs.readdir(currentDirectory, function(err, items) {
    items.forEach(function(it) {
      let file = currentDirectory + '/' + it;
      checkFile(gitRepoPath, file, callback);
    })
  });
}

function checkFile(gitRepoPath, file, callback) {
  fs.stat(file, function(err, stats) {
      if (!stats.isDirectory()) {
        return;
      }
      executeShortLog(gitRepoPath, file, function(result) {
        callback(result);
        checkInt(gitRepoPath, file, callback);
      });
  });
}

function executeShortLog(gitRepoPath, file, callback) {
  exec(shortlogCommand(gitRepoPath, file), { cwd : '/tmp/' }, function(err, stdout, stderr) {
    if (stdout.length == 0) {
      return;
    }

    let result = parseShortLog(file, stdout);
    callback(result);
  });
}

function shortlogCommand(gitRepoPath, file) {
  return 'git -C ' + gitRepoPath + ' shortlog -n -s -- ' + file + ' < /dev/tty';
}

function parseShortLog(file, shortlog) {
  let lines = shortlog.replace(/[\t\r]/g," ").split("\n").filter(filterEmpty);
  let contributors = marshallToContributors(lines);
  return {
    directory: file,
    contributors: contributors
  }
}

function filterEmpty(it) {
  return it && it.length > 0;
}

function marshallToContributors(lines) {
  let contributors = [];
  lines.forEach(function(each) {
    let split = each.trim().split(/(\d+)/g).filter(filterEmpty);
    let contributor = {
      author: split[1].trim(),
      commitCount: split[0].trim()
    };
    contributors.push(contributor)
  });
  return contributors;
}