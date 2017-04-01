class CreateUsersTasks < ActiveRecord::Migration
  def change
    create_table :users_tasks do |t|

      t.timestamps null: false
    end
  end
end
