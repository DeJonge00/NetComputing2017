require 'test_helper'

class Users::TasksControllerTest < ActionController::TestCase
  setup do
    @users_task = users_tasks(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:users_tasks)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create users_task" do
    assert_difference('Users::Task.count') do
      post :create, users_task: {  }
    end

    assert_redirected_to users_task_path(assigns(:users_task))
  end

  test "should show users_task" do
    get :show, id: @users_task
    assert_response :success
  end

  test "should get edit" do
    get :edit, id: @users_task
    assert_response :success
  end

  test "should update users_task" do
    patch :update, id: @users_task, users_task: {  }
    assert_redirected_to users_task_path(assigns(:users_task))
  end

  test "should destroy users_task" do
    assert_difference('Users::Task.count', -1) do
      delete :destroy, id: @users_task
    end

    assert_redirected_to users_tasks_path
  end
end
