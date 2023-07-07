current_branch=$(git rev-parse --abbrev-ref HEAD)

git fetch upstream
git checkout dev
git merge upstream/dev

git push

git checkout $current_branch
