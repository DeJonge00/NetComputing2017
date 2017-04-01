class Users::TasksController < ApplicationController
  before_action :set_users_task, only: [:show, :edit, :update, :destroy]

  # GET /users/tasks
  # GET /users/tasks.json
  def index
    @users_tasks = Users::Task.all
  end

  # GET /users/tasks/1
  # GET /users/tasks/1.json
  def show
  end

  # GET /users/tasks/new
  def new
    @users_task = Users::Task.new
  end

  # GET /users/tasks/1/edit
  def edit
  end

  # POST /users/tasks
  # POST /users/tasks.json
  def create
    @users_task = Users::Task.new(users_task_params)

    respond_to do |format|
      if @users_task.save
        format.html { redirect_to @users_task, notice: 'Task was successfully created.' }
        format.json { render :show, status: :created, location: @users_task }
      else
        format.html { render :new }
        format.json { render json: @users_task.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /users/tasks/1
  # PATCH/PUT /users/tasks/1.json
  def update
    respond_to do |format|
      if @users_task.update(users_task_params)
        format.html { redirect_to @users_task, notice: 'Task was successfully updated.' }
        format.json { render :show, status: :ok, location: @users_task }
      else
        format.html { render :edit }
        format.json { render json: @users_task.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /users/tasks/1
  # DELETE /users/tasks/1.json
  def destroy
    @users_task.destroy
    respond_to do |format|
      format.html { redirect_to users_tasks_url, notice: 'Task was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_users_task
      @users_task = Users::Task.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def users_task_params
      params.fetch(:users_task, {})
    end
end
